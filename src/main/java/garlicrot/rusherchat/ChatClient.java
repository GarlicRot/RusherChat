package garlicrot.rusherchat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;

public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    private static final int MAX_MESSAGE_LENGTH = 256;
    private static final boolean AUTO_RECONNECT = true;
    private static final boolean SHOW_JOIN_MESSAGE = true;

    private static ChatClient instance;
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
                return String.format("%tF %<tT [%s] %s - %s%s%n",
                        record.getMillis(), record.getLevel(), record.getLoggerName(),
                        record.getMessage(), record.getThrown() != null ? " " + record.getThrown() : "");
            }
        });
        logger.addHandler(handler);
    }

    private ChatClient(String host, int port, java.util.function.Consumer<String> onReceive) {
        this.serverUri = URI.create("wss://" + host + ":" + port + "/");
        this.onReceive = onReceive;
        LOGGER.info("ChatClient instance created for " + serverUri);
    }

    public static synchronized ChatClient getInstance(String host, int port, java.util.function.Consumer<String> onReceive) {
        if (instance == null) {
            instance = new ChatClient(host, port, onReceive);
        } else {
            LOGGER.info("Returning existing ChatClient instance for " + instance.serverUri);
        }
        return instance;
    }

    public void connect() {
        if (connected.get() || (wsClient != null && wsClient.isOpen())) return;

        wsClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connected.set(true);
                if (SHOW_JOIN_MESSAGE) onReceive.accept("§7[System] Connected to chat server.");
                LOGGER.info("Connected to " + serverUri);
                startPing();
            }

            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase("pong")) return;

                try {
                    Message msg = gson.fromJson(message, Message.class);
                    String rawUsername = stripColor(msg.getUsername()).toLowerCase();
                    if (ignoredUsers.contains(rawUsername)) return;

                    String display = "[" + msg.getColoredUsername() + "] " + msg.getContent();
                    onReceive.accept(display);
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

        try {
            LOGGER.info("Attempting to connect to " + serverUri);
            wsClient.connectBlocking();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "WebSocket connection failed", e);
            Thread.currentThread().interrupt();
        }
    }

    public void send(String content) {
        if (wsClient == null || !wsClient.isOpen()) {
            sendPrivate("§7[System] Not connected to server.");
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

        String username = Minecraft.getInstance().getUser().getName();
        Message msg = new Message(username, content);
        wsClient.send(gson.toJson(msg));
        LOGGER.fine("Sent message: " + content);
    }

    private void sendPrivate(String message) {
        if (onReceive != null) {
            onReceive.accept(message);
        }
    }

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
            if (!connected.get() && (wsClient == null || !wsClient.isOpen())) connect();
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

    private String stripColor(String input) {
        return input.replaceAll("§[0-9A-FK-ORa-fk-or]", "").toLowerCase();
    }
}
