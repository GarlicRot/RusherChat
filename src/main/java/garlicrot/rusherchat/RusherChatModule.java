package garlicrot.rusherchat;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.setting.BooleanSetting;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RusherChatModule extends ToggleableModule {
    private static final Logger LOGGER = Logger.getLogger(RusherChatModule.class.getName());

    private final BooleanSetting autoReconnect = new BooleanSetting("Auto Reconnect", true);
    private final BooleanSetting showJoinMessage = new BooleanSetting("Show Join Message", true);
    private final BooleanSetting showHistory = new BooleanSetting("Show History", true);

    private RusherChatWindow chatWindow;
    private ChatClient chatClient;

    static {
        // Configure JUL programmatically
        Logger logger = Logger.getLogger("garlicrot.rusherchat");
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);

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

    public RusherChatModule() {
        super("RusherChat", "Chat with other users running the plugin", ModuleCategory.MISC);

        this.registerSettings(
                autoReconnect,
                showJoinMessage,
                showHistory
        );
    }

    @Override
    public void onEnable() {
        LOGGER.info("Enabling RusherChat module");
        chatClient = new ChatClient("rusherchatserver.fly.dev", 443, this::handleIncoming);
        chatClient.setAutoReconnect(autoReconnect.getValue());
        chatClient.setShowJoinMessage(showJoinMessage.getValue());
        chatClient.setShowHistory(showHistory.getValue());
        chatClient.connect();

        if (chatWindow == null) {
            chatWindow = new RusherChatWindow(chatClient, this::handleSend);
            RusherHackAPI.getWindowManager().registerFeature(chatWindow);
            LOGGER.info("Registered RusherChat window");
        }
        chatWindow.setHidden(false);
        LOGGER.fine("RusherChat window shown");
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
            LOGGER.warning("Cannot display message: ChatWindow is null");
        }
    }
}