package garlicrot.rusherchat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    private final String host;
    private final int port;
    private final java.util.function.Consumer<String> onReceive;
    private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listenerThread;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> reconnectTask;

    private final List<String> history = new LinkedList<>();
    private final Set<String> ignoredUsers = new HashSet<>();

    private static final int HISTORY_LIMIT = 50;

    // New configurable behavior
    private boolean autoReconnect = true;
    private boolean showJoinMessage = true;
    private boolean showHistory = true;

    public ChatClient(String host, int port, java.util.function.Consumer<String> onReceive) {
        this.host = host;
        this.port = port;
        this.onReceive = onReceive;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            connected.set(true);
            startListener();
            startPinger();

            if (showJoinMessage) {
                onReceive.accept("§7[System] Connected to chat server.");
            }

            if (showHistory) {
                replayHistory();
            }

            System.out.println("[RusherChat] Connected to chat server.");
        } catch (IOException e) {
            System.err.println("[RusherChat] Failed to connect: " + e.getMessage());
            onReceive.accept("§7[System] Failed to connect. Retrying...");
            if (autoReconnect) {
                startReconnectLoop();
            }
        }
    }

    private void startListener() {
        listenerThread = new Thread(() -> {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("pong")) continue;

                    try {
                        Message msg = gson.fromJson(line, Message.class);

                        String rawUsername = stripColor(msg.getUsername());
                        if (ignoredUsers.contains(rawUsername)) continue;

                        String display = "[" + msg.getUsername() + "] " + msg.getContent();

                        synchronized (history) {
                            history.add(display);
                            if (history.size() > HISTORY_LIMIT) {
                                history.remove(0);
                            }
                        }

                        onReceive.accept(display);
                    } catch (JsonSyntaxException e) {
                        System.err.println("[RusherChat] Invalid JSON: " + line);
                    }
                }
            } catch (IOException e) {
                System.err.println("[RusherChat] Listener error: " + e.getMessage());
            } finally {
                connected.set(false);
                onReceive.accept("§7[System] Disconnected.");
                if (autoReconnect) {
                    onReceive.accept("§7[System] Reconnecting...");
                    startReconnectLoop();
                }
            }
        }, "RusherChat-Listener");
        listenerThread.start();
    }

    private void startPinger() {
        scheduler.scheduleAtFixedRate(() -> {
            if (connected.get() && out != null) {
                out.println("ping");
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void startReconnectLoop() {
        if (reconnectTask != null && !reconnectTask.isCancelled()) return;

        reconnectTask = scheduler.scheduleWithFixedDelay(() -> {
            if (!connected.get()) {
                connect();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void replayHistory() {
        synchronized (history) {
            for (String msg : history) {
                onReceive.accept(msg);
            }
        }
    }

    public void send(String content) {
        if (out == null || socket == null || !socket.isConnected()) return;

        // Handle ignore toggle
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

        // Normal chat message
        String username = Minecraft.getInstance().getUser().getName();
        Message msg = new Message(username, content);
        out.println(gson.toJson(msg));
    }

    public void close() {
        connected.set(false);
        try {
            if (listenerThread != null) listenerThread.interrupt();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}

        scheduler.shutdownNow();
    }

    private String stripColor(String input) {
        return input.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
    }

    // --- Setters for module config ---
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
