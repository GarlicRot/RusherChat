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
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    private static ChatClient instance; // Singleton instance
    private final URI serverUri;
    private final java.util.function.Consumer<String> onReceive;
    private final Gson gson = new Gson();

    private WebSocketClient wsClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> reconnectTask;
    private ScheduledFuture<?> pingTask;

    private final Set<String> ignoredUsers = Collections.synchronizedSet(new HashSet<>()); // Stores lowercase usernames
    private String lastWhisperer; // Tracks the last user who whispered

    private static final boolean AUTO_RECONNECT = true; // Hardcoded default
    private static final boolean SHOW_JOIN_MESSAGE = true; // Hardcoded default

    static {
        Logger logger = Logger.getLogger("garlicrot.rusherchat");
        logger.setLevel(Level.INFO); // Set to INFO for operational logs only
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
        if (connected.get()) {
            LOGGER.info("Already connected to " + serverUri + ", skipping connection attempt.");
            return;
        }
        if (wsClient != null && wsClient.isOpen()) {
            LOGGER.info("WebSocketClient already open for " + serverUri + ", skipping connection.");
            return;
        }

        wsClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connected.set(true);
                String username = Minecraft.getInstance().getUser().getName(); // Optional username
                if (SHOW_JOIN_MESSAGE) onReceive.accept("§7[System] Connected to chat server.");
                LOGGER.info("Connected to " + serverUri + " as " + username);
                startPing();
            }

            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase("pong")) return;

                try {
                    Message msg = gson.fromJson(message, Message.class);
                    if (msg == null || msg.getUsername() == null) {
                        LOGGER.warning("Parsed Message or username is null for: " + message);
                        return;
                    }
                    String rawUsername = stripColor(msg.getUsername()).toLowerCase();
                    if (ignoredUsers.contains(rawUsername)) return;

                    String coloredUsername = msg.getColoredUsername() != null ? msg.getColoredUsername() : rawUsername;
                    String content = msg.getContent() != null ? msg.getContent() : "null";
                    String display;

                    // Handle whispers
                    if (msg.isWhisper()) {
                        String currentUser = stripColor(Minecraft.getInstance().getUser().getName()).toLowerCase();
                        display = (msg.getTarget() != null && msg.getTarget().toLowerCase().equals(currentUser))
                                ? "§d[Whisper from " + coloredUsername + "] " + content
                                : (rawUsername.equals(currentUser))
                                ? "§d[Whisper to " + (msg.getTarget() != null ? msg.getTarget() : "Unknown") + "] " + content
                                : null;
                        if (display != null && onReceive != null) {
                            onReceive.accept(display);
                            if (msg.getTarget() != null && !msg.getTarget().toLowerCase().equals(currentUser)) {
                                lastWhisperer = rawUsername; // Update last whisperer
                            }
                        }
                    } else {
                        // Handle broadcasts
                        display = "[" + coloredUsername + "] " + content;
                        if (onReceive != null) {
                            LOGGER.info("Received broadcast: " + display); // Debug log
                            onReceive.accept(display);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    LOGGER.log(Level.SEVERE, "Invalid JSON: " + message, e);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected error processing message: " + message, e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                connected.set(false);
                onReceive.accept("§7[System] Disconnected: " + reason + " (Code: " + code + ")");
                LOGGER.info("Connection closed: Code " + code + ", Reason: " + reason);
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

    private void startReconnectLoop() {
        if (reconnectTask != null && !reconnectTask.isCancelled()) {
            LOGGER.info("Reconnect task already running, skipping.");
            return;
        }

        reconnectTask = scheduler.scheduleWithFixedDelay(() -> {
            if (!connected.get() && (wsClient == null || !wsClient.isOpen())) {
                LOGGER.info("Attempting to reconnect to " + serverUri);
                connect();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void startPing() {
        if (pingTask != null && !pingTask.isCancelled()) {
            LOGGER.info("Ping task already running, skipping.");
            return;
        }

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
            LOGGER.info("Ping task stopped.");
        }
    }

    public void send(String content) {
        if (wsClient == null || !wsClient.isOpen()) {
            sendPrivate("§7[System] Not connected to server.");
            return;
        }
        String username = Minecraft.getInstance().getUser().getName();
        String rawUsername = stripColor(username).toLowerCase();

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

        if (content.startsWith("/w ") || content.startsWith("/whisper ")) {
            String[] parts = content.split(" ", 3);
            if (parts.length < 3 || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
                sendPrivate("§7[System] Usage: /w <username> <message>");
                return;
            }
            String target = parts[1].trim();
            String message = parts[2].trim();
            Message msg = new Message(username, message, null, target, true);
            wsClient.send(gson.toJson(msg));
            LOGGER.info("Sent whisper to " + target + ": " + message);
            return;
        }

        if (content.startsWith("/r ") || content.startsWith("/reply ")) {
            String[] parts = content.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                sendPrivate("§7[System] Usage: /r <message> (reply to last whisperer)");
                return;
            }
            if (lastWhisperer == null) {
                sendPrivate("§7[System] No recent whisperer to reply to.");
                return;
            }
            String message = parts[1].trim();
            Message msg = new Message(username, message, null, lastWhisperer, true);
            wsClient.send(gson.toJson(msg));
            LOGGER.info("Sent reply to " + lastWhisperer + ": " + message);
            return;
        }

        Message msg = new Message(username, content);
        wsClient.send(gson.toJson(msg));
        LOGGER.info("Sent message: " + content);
    }

    private void sendPrivate(String message) {
        if (onReceive != null) {
            LOGGER.info("Sending private message: " + message); // Debug log
            onReceive.accept(message);
        } else {
            LOGGER.warning("onReceive callback is null, cannot send private message.");
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
                LOGGER.info("Scheduler shut down.");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            LOGGER.log(Level.WARNING, "Scheduler shutdown interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private String stripColor(String input) {
        return input != null ? input.replaceAll("§[0-9A-FK-ORa-fk-or]", "").toLowerCase() : "";
    }
}