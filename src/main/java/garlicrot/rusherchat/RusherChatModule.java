package garlicrot.rusherchat;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RusherChatModule extends ToggleableModule {
    private static final Logger LOGGER = Logger.getLogger(RusherChatModule.class.getName());
    private static RusherChatModule instance; // Singleton instance

    private RusherChatWindow chatWindow;
    private ChatClient chatClient;
    private final List<String> messageQueue = new ArrayList<>(); // Queue for messages before window initialization

    static {
        // Configure JUL programmatically
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

    // Private constructor to enforce singleton
    private RusherChatModule() {
        super("RusherChat", "Chat with other users running the plugin", ModuleCategory.MISC);
        LOGGER.info("RusherChatModule instance created");
    }

    // Singleton getter
    public static synchronized RusherChatModule getInstance() {
        if (instance == null) {
            instance = new RusherChatModule();
        } else {
            LOGGER.info("Returning existing RusherChatModule instance");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        LOGGER.info("Enabling RusherChat module");
        if (chatClient == null) {
            chatClient = new ChatClient("rusherchatserver.fly.dev", 443, this::handleIncoming);
            chatClient.connect();
        } else {
            LOGGER.info("ChatClient already initialized, skipping creation");
        }

        if (chatWindow == null) {
            chatWindow = new RusherChatWindow(chatClient, this::handleSend);
            RusherHackAPI.getWindowManager().registerFeature(chatWindow);
            LOGGER.info("Registered RusherChat window");
        }
        chatWindow.setHidden(false);
        LOGGER.fine("RusherChat window shown");
        // Process any queued messages after initialization
        if (!messageQueue.isEmpty()) {
            for (String msg : messageQueue) {
                handleIncoming(msg);
            }
            messageQueue.clear();
        }
    }

    @Override
    public void onDisable() {
        LOGGER.info("Disabling RusherChat module");
        if (chatWindow != null) {
            chatWindow.setHidden(true);
            LOGGER.fine("RusherChat window hidden");
        }
        if (chatClient != null) {
            chatClient.close();
            chatClient = null; // Reset to allow reinitialization
            LOGGER.fine("ChatClient closed");
        }
    }

    private void handleSend(String message) {
        if (chatClient != null) {
            chatClient.send(message);
            LOGGER.fine("Sent message: " + message);
        } else {
            LOGGER.warning("Cannot send message: ChatClient is null");
        }
    }

    private void handleIncoming(String message) {
        if (chatWindow != null) {
            chatWindow.addMessage(message);
            LOGGER.fine("Received message: " + message);
        } else {
            LOGGER.warning("ChatWindow not initialized, queuing message: " + message);
            messageQueue.add(message);
        }
    }
}