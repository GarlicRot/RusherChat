package garlicrot.rusherchat;

import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.RichTextView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RusherChatWindow extends ResizeableWindow {

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_GRAY = 0xFFAAAAAA;
    private static final int COLOR_BLUE = 0xFFA0A0FF;
    private static final int COLOR_LIGHT_GRAY = 0xFFCCCCCC;

    private final RichTextView messageView;
    private final RichTextView onlineView;
    private final TabbedView tabView;

    private final List<String> messages = new ArrayList<>();
    private final List<String> onlineUsers = new ArrayList<>();

    public RusherChatWindow(ChatClient client, Consumer<String> onSend) {
        super("RusherChat", 100, 100, 600, 250);

        this.setMinWidth(300);
        this.setMinHeight(200);

        this.messageView = new RichTextView("Chat", this);
        this.onlineView = new RichTextView("Online", this);

        final TextFieldComponent inputField = new TextFieldComponent(this, "Enter message...", 100);
        final ButtonComponent sendButton = new ButtonComponent(this, "Send", () -> {
            // The input bar is persistent below the tabs, but it only sends from the Chat tab.
            if (!isChatTabActive()) {
                return;
            }

            String input = inputField.getValue().trim();
            if (!input.isEmpty()) {
                onSend.accept(input);
                inputField.setValue("");
            }
        });

        inputField.setReturnCallback(str -> sendButton.onClick());

        final ComboContent bottomBar = new ComboContent(this);
        bottomBar.addContent(inputField, ComboContent.AnchorSide.LEFT);
        bottomBar.addContent(sendButton, ComboContent.AnchorSide.RIGHT);

        // RichTextView tabs directly + persistent input bar below them.
        this.tabView = new TabbedView(this, List.of(messageView, onlineView, bottomBar));
    }

    private boolean isChatTabActive() {
        return tabView == null || tabView.getActiveTabView() == messageView;
    }

    public void addMessage(String msg) {
        if (messageView == null || msg == null || msg.isBlank()) return;

        messages.add(msg);

        while (messages.size() > 200) {
            messages.remove(0);
        }

        rebuildMessages();
    }

    private void rebuildMessages() {
        if (messageView == null) return;

        messageView.clear();

        for (String message : messages) {
            messageView.add(message, COLOR_WHITE);
        }
    }

    public void setOnlineUsers(List<String> users) {
        if (onlineView == null) return;

        List<String> newUsers = new ArrayList<>();
        if (users != null) {
            newUsers.addAll(users);
        }

        if (newUsers.equals(onlineUsers)) {
            return;
        }

        onlineUsers.clear();
        onlineUsers.addAll(newUsers);

        rebuildOnlineUsers();
    }

    private void rebuildOnlineUsers() {
        if (onlineView == null) return;

        onlineView.clear();
        onlineView.add("§bOnline users: §f" + onlineUsers.size(), COLOR_BLUE);

        if (onlineUsers.isEmpty()) {
            onlineView.add("§7Nobody online yet.", COLOR_GRAY);
            return;
        }

        for (String user : onlineUsers) {
            onlineView.add("§7- §f" + user, COLOR_LIGHT_GRAY);
        }
    }

    @Override
    public WindowView getRootView() {
        return tabView;
    }

}
