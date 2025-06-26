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

public class ChatClient {
    private final URI serverUri;
    private final java.util.function.Consumer<String> onReceive;
    private final Gson gson = new Gson();

    private WebSocketClient wsClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> reconnectTask;

    private final List<String> history = new LinkedList<>();
    private final Set<String> ignoredUsers = new HashSet<>();

    private static final int HISTORY_LIMIT = 50;

    private boolean autoReconnect = true;
    private boolean showJoinMessage = true;
    private boolean showHistory = true;

    public ChatClient(String host, int port, java.util.function.Consumer<String> onReceive) {
        this.serverUri = URI.create("ws://" + host + ":" + port + "/");
        this.onReceive = onReceive;
    }

    public void connect() {
        wsClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connected.set(true);
                if (showJoinMessage) onReceive.accept("§7[System] Connected to chat server.");
                if (showHistory) replayHistory();
                System.out.println("[RusherChat] Connected to chat server.");
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
                    System.err.println("[RusherChat] Invalid JSON: " + message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                connected.set(false);
                onReceive.accept("§7[System] Disconnected.");
                if (autoReconnect) {
                    onReceive.accept("§7[System] Reconnecting...");
                    startReconnectLoop();
                }
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("[RusherChat] WebSocket error: " + ex.getMessage());
            }
        };

        try {
            wsClient.connectBlocking();
        } catch (InterruptedException e) {
            System.err.println("[RusherChat] WebSocket connection failed: " + e.getMessage());
        }
    }

    private void startReconnectLoop() {
        if (reconnectTask != null && !reconnectTask.isCancelled()) return;

        reconnectTask = scheduler.scheduleWithFixedDelay(() -> {
            if (!connected.get()) connect();
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void replayHistory() {
        synchronized (history) {
            for (String msg : history) onReceive.accept(msg);
        }
    }

    public void send(String content) {
        if (wsClient == null || !wsClient.isOpen()) return;

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
