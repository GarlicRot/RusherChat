package garlicrot.rusherchat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    private final URI serverUri;
    private final java.util.function.Consumer<String> onReceive;
    private final Gson gson = new Gson();

    private WebSocketClient wsClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> reconnectTask;
    private ScheduledFuture<?> pingTask;

    private final List<String> history = new LinkedList<>();
    private final Set<String> ignoredUsers = new HashSet<>();

    private static final int HISTORY_LIMIT = 50;

    private boolean autoReconnect = true;
    private boolean showJoinMessage = true;
    private boolean showHistory = true;

    static {
        // Configure JUL programmatically
        Logger logger = Logger.getLogger("garlicrot.rusherchat");
        logger.setLevel(Level.FINE); // Enable detailed logging
        logger.setUseParentHandlers(false); // Remove default handlers

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
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

    public ChatClient(String host, int port, java.util.function.Consumer<String> onReceive) {
        this.serverUri = URI.create("wss://" + host + ":" + port + "/");
        this.onReceive = onReceive;
    }

    public void connect() {
        wsClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connected.set(true);
                if (showJoinMessage) onReceive.accept("§7[System] Connected to chat server.");
                if (showHistory) replayHistory();
                LOGGER.info("Connected to " + serverUri);
                startPing();
            }

            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase("pong")) return;

                try {
                    Message msg = gson.fromJson(message, Message.class);
                    String rawUsername = stripColor(msg.getUsername());
                    if (ignoredUsers.contains(rawUsername)) return;

                    String display = "[" + msg.getUsername() + "] " + msg.getContent();
                    synchronized (history) {
                        history.add(display);
                        if (history.size() > HISTORY_LIMIT) history.remove(0);
                    }
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
                if (autoReconnect) {
                    onReceive.accept("§7[System] Reconnecting...");
                    startReconnectLoop();
                }
            }

            @Override
            public void onError(Exception ex) {
                LOGGER.log(Level.SEVERE, "WebSocket error", ex); // Replaced printStackTrace
            }
        };

        try {
            LOGGER.info("Attempting to connect to " + serverUri);
            wsClient.connectBlocking();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "WebSocket connection failed", e); // Replaced printStackTrace
        }
    }

    private void startReconnectLoop() {
        if (reconnectTask != null && !reconnectTask.isCancelled()) return;

        reconnectTask = scheduler.scheduleWithFixedDelay(() -> {
            if (!connected.get()) connect();
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void startPing() {
        if (pingTask != null && !pingTask.isCancelled()) return;

        pingTask = scheduler.scheduleAtFixedRate(() -> {
            if (wsClient != null && wsClient.isOpen()) {
                wsClient.send("ping");
                LOGGER.fine("Sent ping to server");
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void stopPing() {
        if (pingTask != null) {
            pingTask.cancel(false);
            pingTask = null;
        }
    }

    private void replayHistory() {
        synchronized (history) {
            for (String msg : history) onReceive.accept(msg);
        }
    }

    public void send(String content) {
        if (wsClient == null || !wsClient.isOpen()) {
            onReceive.accept("§7[System] Not connected to server.");
            return;
        }

        if (content.startsWith("/i ") || content.startsWith("/ignore ")) {
            String[] parts = content.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                onReceive.accept("§7[System] Usage: /ignore <username>");
                return;
            }

            String target = parts[1].trim();
            if (ignoredUsers.contains(target)) {
                ignoredUsers.remove(target);
                onReceive.accept("§7[System] Unignored " + target);
            } else {
                ignoredUsers.add(target);
                onReceive.accept("§7[System] Now ignoring " + target);
            }
            return;
        }

        String username = Minecraft.getInstance().getUser().getName();
        Message msg = new Message(username, content);
        wsClient.send(gson.toJson(msg));
    }

    public void close() {
        connected.set(false);
        stopPing();
        if (wsClient != null) wsClient.close();
        scheduler.shutdownNow();
    }

    private String stripColor(String input) {
        return input.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
    }

    // Setters
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public void setShowJoinMessage(boolean showJoinMessage) {
        this.showJoinMessage = showJoinMessage;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }
}