package garlicrot.rusherchat;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.setting.BooleanSetting;

public class RusherChatModule extends ToggleableModule {

    private final BooleanSetting autoReconnect = new BooleanSetting("Auto Reconnect", true);
    private final BooleanSetting showJoinMessage = new BooleanSetting("Show Join Message", true);
    private final BooleanSetting showHistory = new BooleanSetting("Show History", true);

    private RusherChatWindow chatWindow;
    private ChatClient chatClient;

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
        chatClient = new ChatClient("rusherchatserver.fly.dev", 42424, this::handleIncoming);
        chatClient.setAutoReconnect(autoReconnect.getValue());
        chatClient.setShowJoinMessage(showJoinMessage.getValue());
        chatClient.setShowHistory(showHistory.getValue());
        chatClient.connect();

        if (chatWindow == null) {
            chatWindow = new RusherChatWindow(chatClient, this::handleSend);
            RusherHackAPI.getWindowManager().registerFeature(chatWindow);
        }
        chatWindow.setHidden(false);
    }

    @Override
    public void onDisable() {
        if (chatWindow != null) {
            chatWindow.setHidden(true);
        }
        if (chatClient != null) {
            chatClient.close();
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
        }
    }
}
