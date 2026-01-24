package garlicrot.rusherchat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.*;

public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    private static final int MAX_MESSAGE_LENGTH = 256;
    private static final boolean AUTO_RECONNECT = true;
    private static final boolean SHOW_JOIN_MESSAGE = true;

    private static final SecureRandom RAND = new SecureRandom();

    private final URI serverUri;
    private final java.util.function.Consumer<String> onReceive;
    private final java.util.function.Consumer<List<String>> onOnlineListUpdate;
    private final Gson gson = new Gson();

    private WebSocketClient wsClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> reconnectTask;
    private ScheduledFuture<?> pingTask;

    private final Set<String> ignoredUsers = Collections.synchronizedSet(new HashSet<>());
    private long lastSendTime = 0;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final ConcurrentMap<String, PublicKey> knownPublicKeys = new ConcurrentHashMap<>();
    private String lastWhisperUser = null;

    static {
        Logger logger = Logger.getLogger("garlicrot.rusherchat");
        logger.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format(
                        "%tF %<tT [%s] %s - %s%s%n",
                        record.getMillis(),
                        record.getLevel(),
                        record.getLoggerName(),
                        record.getMessage(),
                        record.getThrown() != null ? " " + record.getThrown() : ""
                );
            }
        });
        logger.addHandler(handler);
    }

    public ChatClient(
            String host,
            int port,
            java.util.function.Consumer<String> onReceive,
            java.util.function.Consumer<List<String>> onOnlineListUpdate
    ) {
        if (host.startsWith("ws://") || host.startsWith("wss://")) {
            this.serverUri = URI.create(host);
        } else {
            this.serverUri = URI.create("ws://" + host + ":" + port + "/");
        }

        this.onReceive = onReceive;
        this.onOnlineListUpdate = onOnlineListUpdate;
        LOGGER.fine("ChatClient instance created for " + serverUri);
        initKeyPair();
    }

    // --- Keypair / public key helpers ---

    private void initKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            // E2EE state
            KeyPair keyPair = kpg.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            LOGGER.fine("Initialized RSA keypair for RusherChat E2EE");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate RSA keypair", e);
        }
    }

    private String getPublicKeyBase64() {
        if (publicKey == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // --- E2EE helpers ---

    private String encryptFor(String targetUsername, String plainText) throws Exception {
        PublicKey targetKey = knownPublicKeys.get(targetUsername.toLowerCase());
        if (targetKey == null) {
            throw new IllegalStateException("No public key for user: " + targetUsername);
        }

        // AES key
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        SecretKey aesKey = kg.generateKey();

        // Encrypt AES key with recipient's RSA key
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, targetKey);
        byte[] encKey = rsa.doFinal(aesKey.getEncoded());

        // Encrypt message with AES-GCM
        byte[] iv = new byte[12];
        RAND.nextBytes(iv);

        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] cipherBytes = aes.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        int keyLen = encKey.length;
        if (keyLen > 65535) {
            throw new IllegalStateException("Encrypted key too long");
        }

        byte[] envelope = new byte[2 + keyLen + iv.length + cipherBytes.length];
        envelope[0] = (byte) ((keyLen >>> 8) & 0xFF);
        envelope[1] = (byte) (keyLen & 0xFF);
        System.arraycopy(encKey, 0, envelope, 2, keyLen);
        System.arraycopy(iv, 0, envelope, 2 + keyLen, iv.length);
        System.arraycopy(cipherBytes, 0, envelope, 2 + keyLen + iv.length, cipherBytes.length);

        return Base64.getEncoder().encodeToString(envelope);
    }

    private String decryptWhisperEnvelope(String b64Envelope) throws Exception {
        if (privateKey == null) {
            throw new IllegalStateException("No private key available for decryption");
        }

        byte[] envelope = Base64.getDecoder().decode(b64Envelope);
        if (envelope.length < 2 + 1 + 12) {
            throw new IllegalArgumentException("Envelope too short");
        }

        int keyLen = ((envelope[0] & 0xFF) << 8) | (envelope[1] & 0xFF);
        int offset = 2;

        if (keyLen <= 0 || keyLen + 2 + 12 > envelope.length) {
            throw new IllegalArgumentException("Invalid envelope key length");
        }

        byte[] encKey = Arrays.copyOfRange(envelope, offset, offset + keyLen);
        offset += keyLen;

        byte[] iv = Arrays.copyOfRange(envelope, offset, offset + 12);
        offset += 12;

        byte[] cipherBytes = Arrays.copyOfRange(envelope, offset, envelope.length);

        // RSA decrypt AES key
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsa.doFinal(encKey);
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // AES-GCM decrypt
        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] plainBytes = aes.doFinal(cipherBytes);

        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    private void handleUserKeySystemMessage(String content) {
        // Expected format: USER_KEY:username:base64Key
        String[] parts = content.split(":", 3);
        if (parts.length != 3) {
            return;
        }

        String username = parts[1];
        String b64 = parts[2];

        try {
            byte[] decoded = Base64.getDecoder().decode(b64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(spec);
            knownPublicKeys.put(username.toLowerCase(), pk);
            LOGGER.fine("Stored public key for user " + username);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse user public key for " + username, e);
        }
    }

    private void handleOnlineListSystemMessage(String content) {
        // Expected format: ONLINE_LIST:name1,name2,...
        final String prefix = "ONLINE_LIST:";
        if (!content.startsWith(prefix)) {
            return;
        }

        String payload = content.substring(prefix.length());
        List<String> users = new ArrayList<>();

        if (!payload.isBlank()) {
            String[] split = payload.split(",");
            for (String s : split) {
                String name = s.trim();
                if (!name.isEmpty()) {
                    users.add(name);
                }
            }
        }

        if (onOnlineListUpdate != null) {
            onOnlineListUpdate.accept(users);
        }
    }

    // --- Connection logic ---

    public void connect() {
        if (connected.get() || (wsClient != null && wsClient.isOpen())) return;

        wsClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connected.set(true);

                String username = Minecraft.getInstance().getUser().getName();
                Message login = new Message(Message.Type.LOGIN, username, null, null, null, false);

                // Attach our public key without spamming logs
                String pubKeyB64 = getPublicKeyBase64();
                if (pubKeyB64 != null && !pubKeyB64.isEmpty()) {
                    login.setPublicKey(pubKeyB64);
                    LOGGER.fine("[E2EE] Attaching publicKey to LOGIN (length=" + pubKeyB64.length() + " chars)");
                } else {
                    LOGGER.warning("[E2EE] Public key is null/empty at LOGIN time – not attaching key.");
                }

                wsClient.send(gson.toJson(login));

                if (SHOW_JOIN_MESSAGE) {
                    onReceive.accept("§7[System] Connected to chat server.");
                }
                LOGGER.info("Connected to " + serverUri + " as " + username);
                startPing();
            }

            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase("pong")) return;

                try {
                    Message msg = gson.fromJson(message, Message.class);
                    if (msg == null) return;

                    String content = msg.getContent();
                    Message.Type type = msg.getType();

                    // SYSTEM: key announcements + online list
                    if (type == Message.Type.SYSTEM && content != null) {
                        if (content.startsWith("USER_KEY:")) {
                            handleUserKeySystemMessage(content);
                            return;
                        }
                        if (content.startsWith("ONLINE_LIST:")) {
                            handleOnlineListSystemMessage(content);
                            return;
                        }
                        // fall through for "normal" system messages so they still show up in chat
                    }

                    // Decrypt E2EE whispers
                    if (type == Message.Type.WHISPER && content != null && !content.isEmpty()) {
                        try {
                            content = decryptWhisperEnvelope(content);
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Failed to decrypt whisper envelope; showing raw content", e);
                        }
                    }

                    if (content == null || content.trim().isEmpty()) return;

                    String rawUsername = stripColor(msg.getUsername()).toLowerCase();
                    if (ignoredUsers.contains(rawUsername)) return;

                    if (type == Message.Type.WHISPER) {
                        // crude partner extraction from "[Whisper] Name" / "[Whisper ->] Name"
                        String stripped = rawUsername
                                .replace("[whisper]", "")
                                .replace("[whisper ->]", "")
                                .trim();
                        if (!stripped.isEmpty()) {
                            lastWhisperUser = stripped;
                        }
                    }

                    onReceive.accept(formatDisplayMessage(msg, content));
                } catch (JsonSyntaxException e) {
                    LOGGER.log(Level.SEVERE, "Invalid JSON: " + message, e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                connected.set(false);
                onReceive.accept("§7[System] Disconnected: " + reason + " (Code: " + code + ")");
                stopPing();
                if (AUTO_RECONNECT) {
                    onReceive.accept("§7[System] Reconnecting...");
                    startReconnectLoop();
                }
            }

            @Override
            public void onError(Exception ex) {
                LOGGER.log(Level.SEVERE, "WebSocket error", ex);
            }
        };

        // Enable TLS for wss:// connections
        if ("wss".equalsIgnoreCase(serverUri.getScheme())) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
                wsClient.setSocketFactory(sslContext.getSocketFactory());
                LOGGER.fine("SSL context configured for WSS connection.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to set up SSL for WSS connection", e);
            }
        }

        try {
            LOGGER.fine("Attempting to connect to " + serverUri);
            wsClient.connectBlocking();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "WebSocket connection failed", e);
            Thread.currentThread().interrupt();
        }
    }

    // --- Sending messages ---

    public void send(String content) {
        if (wsClient == null || !wsClient.isOpen()) {
            sendPrivate("§7[System] Not connected to server.");
            return;
        }

        // Local /ignore
        if (content.startsWith("/i ") || content.startsWith("/ignore ")) {
            String[] parts = content.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                sendPrivate("§7[System] Usage: /ignore <username>");
                return;
            }

            String target = parts[1].trim().toLowerCase();
            if (ignoredUsers.contains(target)) {
                ignoredUsers.remove(target);
                sendPrivate("§7[System] Unignored " + parts[1]);
            } else {
                ignoredUsers.add(target);
                sendPrivate("§7[System] Now ignoring " + parts[1]);
            }
            return;
        }

        // E2EE whispers handled client-side
        if (content.startsWith("/w ") || content.startsWith("/whisper ")) {
            handleWhisperSend(content);
            return;
        }

        // E2EE reply handled client-side
        if (content.startsWith("/r ") || content.startsWith("/reply ")) {
            handleReplySend(content);
            return;
        }

        if (content.length() > MAX_MESSAGE_LENGTH) {
            sendPrivate("§7[System] Message too long. Max " + MAX_MESSAGE_LENGTH + " characters.");
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastSendTime < 1000) {
            sendPrivate("§7[System] You are sending messages too quickly. Please slow down.");
            return;
        }
        lastSendTime = now;

        String username = Minecraft.getInstance().getUser().getName();
        Message msg = new Message(Message.Type.CHAT, username, content);
        wsClient.send(gson.toJson(msg));
        LOGGER.fine("Sent message: " + content);
    }

    private void handleWhisperSend(String raw) {
        String[] parts = raw.split(" ", 3);
        if (parts.length < 3) {
            sendPrivate("§7[System] Usage: /w <username> <message>");
            return;
        }

        String target = parts[1].trim();
        String msgText = parts[2];

        if (msgText.length() > MAX_MESSAGE_LENGTH) {
            sendPrivate("§7[System] Whisper too long. Max " + MAX_MESSAGE_LENGTH + " characters.");
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastSendTime < 1000) {
            sendPrivate("§7[System] You are sending messages too quickly. Please slow down.");
            return;
        }
        lastSendTime = now;

        try {
            String encrypted = encryptFor(target, msgText);

            String username = Minecraft.getInstance().getUser().getName();
            Message msg = new Message(
                    Message.Type.WHISPER,
                    username,
                    encrypted,
                    null,
                    target,
                    true
            );
            wsClient.send(gson.toJson(msg));

            // Local echo as plaintext
            String display = "§5[Whisper ->] " + target + ": " + msgText;
            onReceive.accept(display);

            lastWhisperUser = target;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to encrypt whisper", e);
            sendPrivate("§7[System] Failed to send whisper (encryption error or missing key).");
        }
    }

    private void handleReplySend(String raw) {
        String[] parts = raw.split(" ", 2);
        if (parts.length < 2) {
            sendPrivate("§7[System] Usage: /r <message>");
            return;
        }

        if (lastWhisperUser == null || lastWhisperUser.isEmpty()) {
            sendPrivate("§7[System] No recent user to reply to.");
            return;
        }

        String target = lastWhisperUser;
        String msgText = parts[1];

        if (msgText.length() > MAX_MESSAGE_LENGTH) {
            sendPrivate("§7[System] Whisper too long. Max " + MAX_MESSAGE_LENGTH + " characters.");
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastSendTime < 1000) {
            sendPrivate("§7[System] You are sending messages too quickly. Please slow down.");
            return;
        }
        lastSendTime = now;

        try {
            String encrypted = encryptFor(target, msgText);

            String username = Minecraft.getInstance().getUser().getName();
            Message msg = new Message(
                    Message.Type.WHISPER,
                    username,
                    encrypted,
                    null,
                    target,
                    true
            );
            wsClient.send(gson.toJson(msg));

            String display = "§5[Whisper ->] " + target + ": " + msgText;
            onReceive.accept(display);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to encrypt whisper reply", e);
            sendPrivate("§7[System] Failed to send whisper (encryption error or missing key).");
        }
    }

    private void sendPrivate(String message) {
        if (onReceive != null) {
            onReceive.accept(message);
        }
    }

    // --- Lifecycle ---

    public void close() {
        connected.set(false);
        stopPing();
        if (wsClient != null) {
            wsClient.close();
            wsClient = null;
            LOGGER.fine("WebSocketClient closed.");
        }
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void startReconnectLoop() {
        if (reconnectTask != null && !reconnectTask.isCancelled()) return;

        reconnectTask = scheduler.scheduleWithFixedDelay(() -> {
            if (!connected.get() && (wsClient == null || !wsClient.isOpen())) {
                connect();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void startPing() {
        if (pingTask != null && !pingTask.isCancelled()) return;

        pingTask = scheduler.scheduleAtFixedRate(() -> {
            if (wsClient != null && wsClient.isOpen()) {
                wsClient.send("ping");
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void stopPing() {
        if (pingTask != null) {
            pingTask.cancel(false);
            pingTask = null;
        }
    }

    // --- Display helpers ---

    private String formatDisplayMessage(Message msg, String content) {
        String usernameDisplay = msg.getColoredUsername() != null
                ? msg.getColoredUsername()
                : msg.getUsername();

        if (usernameDisplay.contains("[Whisper ->]")) {
            return "§5" + usernameDisplay + ": " + content;
        } else if (usernameDisplay.contains("[Whisper]")) {
            return "§d" + usernameDisplay + ": " + content;
        } else if (usernameDisplay.contains("[System]")) {
            return "§7" + content;
        } else {
            return usernameDisplay + ": " + content;
        }
    }

    private String stripColor(String input) {
        return input.replaceAll("§[0-9A-FK-ORa-fk-or]", "").toLowerCase();
    }
}
