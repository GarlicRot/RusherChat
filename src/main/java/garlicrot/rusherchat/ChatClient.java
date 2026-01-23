package garlicrot.rusherchat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;
import java.util.Arrays;

public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    private static final int MAX_MESSAGE_LENGTH = 256;
    private static final boolean AUTO_RECONNECT = true;
    private static final boolean SHOW_JOIN_MESSAGE = true;

    // Must match server secret!
    private static final String WHISPER_SECRET = "rusherchat-whisper-key-01";
    private static SecretKeySpec WHISPER_KEY;

    private final URI serverUri;
    private final java.util.function.Consumer<String> onReceive;
    private final Gson gson = new Gson();

    private WebSocketClient wsClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> reconnectTask;
    private ScheduledFuture<?> pingTask;

    private final Set<String> ignoredUsers = Collections.synchronizedSet(new HashSet<>());
    private long lastSendTime = 0;

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

    public ChatClient(String host, int port, java.util.function.Consumer<String> onReceive) {
        // If host is already a full ws/wss URL, use it directly (e.g. wss://rusherchat.smokelog.org)
        if (host.startsWith("ws://") || host.startsWith("wss://")) {
            this.serverUri = URI.create(host);
        } else {
            // Legacy style: host + port
            this.serverUri = URI.create("ws://" + host + ":" + port + "/");
        }

        this.onReceive = onReceive;
        LOGGER.info("ChatClient instance created for " + serverUri);
    }

    // --- Encryption helpers (must mirror server) ---

    private static SecretKeySpec getWhisperKey() throws Exception {
        if (WHISPER_KEY == null) {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(WHISPER_SECRET.getBytes(StandardCharsets.UTF_8));
            WHISPER_KEY = new SecretKeySpec(key, "AES");
        }
        return WHISPER_KEY;
    }

    private static String decryptWhisper(String cipherTextB64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherTextB64);
        if (combined.length < 13) {
            throw new IllegalArgumentException("Whisper payload too short");
        }

        byte[] iv = Arrays.copyOfRange(combined, 0, 12);
        byte[] cipherBytes = Arrays.copyOfRange(combined, 12, combined.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, getWhisperKey(), new GCMParameterSpec(128, iv));
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
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

                    // Decrypt whispers
                    if (msg.getType() == Message.Type.WHISPER && content != null && !content.isEmpty()) {
                        try {
                            content = decryptWhisper(content);
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Failed to decrypt whisper; showing raw content", e);
                        }
                    }

                    if (content == null || content.trim().isEmpty()) return;

                    String rawUsername = stripColor(msg.getUsername()).toLowerCase();
                    if (ignoredUsers.contains(rawUsername)) return;

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
                LOGGER.info("SSL context configured for WSS connection.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to set up SSL for WSS connection", e);
            }
        }

        try {
            LOGGER.info("Attempting to connect to " + serverUri);
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

        // Local /ignore command
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
            LOGGER.info("WebSocketClient closed.");
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
