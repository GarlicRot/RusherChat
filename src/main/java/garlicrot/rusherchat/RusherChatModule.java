package garlicrot.rusherchat;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class RusherChatModule extends ToggleableModule {

    private static final Logger LOGGER = Logger.getLogger(RusherChatModule.class.getName());
    private static RusherChatModule instance;

    private static final String CHAT_ENDPOINT = "wss://rusherchat.smokelog.org";

    private RusherChatWindow chatWindow;
    private ChatClient chatClient;
    private final List<String> messageQueue = new ArrayList<>();

    static {
        Logger logger = Logger.getLogger("garlicrot.rusherchat");
        logger.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord r) {
                return String.format(
                        "%tF %<tT [%s] %s - %s%n",
                        r.getMillis(), r.getLevel(), r.getLoggerName(), r.getMessage()
                );
            }
        });
        logger.addHandler(handler);
    }

    private RusherChatModule() {
        super("RusherChat", "Shared chat for users running the plugin", ModuleCategory.MISC);
    }

    public static synchronized RusherChatModule getInstance() {
        if (instance == null) instance = new RusherChatModule();
        return instance;
    }

    @Override
    public void onEnable() {
        if (chatClient == null) {
            chatClient = new ChatClient(CHAT_ENDPOINT, 0, this::handleIncoming);
            chatClient.connect();
        }

        if (chatWindow == null) {
            chatWindow = new RusherChatWindow(chatClient, this::handleSend);
            RusherHackAPI.getWindowManager().registerFeature(chatWindow);
        }

        chatWindow.setHidden(false);

        if (!messageQueue.isEmpty()) {
            messageQueue.forEach(this::handleIncoming);
            messageQueue.clear();
        }
    }

    @Override
    public void onDisable() {
        if (chatWindow != null) {
            chatWindow.setHidden(true);
        }

        if (chatClient != null) {
            chatClient.close();
            chatClient = null;
        }
    }

    private void handleSend(String message) {
        if (chatClient != null) {
            chatClient.send(message);
        }
    }

    private void handleIncoming(String message) {
        if (chatWindow != null) {
            chatWindow.addMessage(message);
        } else {
            messageQueue.add(message);
        }
    }
}
