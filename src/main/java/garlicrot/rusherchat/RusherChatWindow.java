package garlicrot.rusherchat;

import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.RichTextView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.SimpleView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.util.List;
import java.util.function.Consumer;

public class RusherChatWindow extends ResizeableWindow {

    private final RichTextView messageView;
    private final RichTextView onlineView;
    private final TabbedView tabView;

    public RusherChatWindow(ChatClient client, Consumer<String> onSend) {
        super("RusherChat", 100, 100, 600, 250);

        this.setMinWidth(300);
        this.setMinHeight(200);

        // --- Chat tab: message log (main content) ---

        this.messageView = new RichTextView("Chat", this);
        final SimpleView chatTab = new SimpleView("Chat", this, List.of(messageView));

        // --- Online tab: list of online users ---

        this.onlineView = new RichTextView("Online", this);
        final SimpleView onlineTab = new SimpleView("Online", this, List.of(onlineView));

        // --- Bottom bar: input + Send button (global footer, like RusherMan) ---

        final TextFieldComponent inputField = new TextFieldComponent(this, "Enter message...", 100);
        final ButtonComponent sendButton = new ButtonComponent(this, "Send", () -> {
            String input = inputField.getValue().trim();
            if (!input.isEmpty()) {
                onSend.accept(input);
                inputField.setValue("");
            }
        });

        // Enter to send
        inputField.setReturnCallback(str -> sendButton.onClick());

        final ComboContent bottomBar = new ComboContent(this);
        bottomBar.addContent(inputField, ComboContent.AnchorSide.LEFT);
        bottomBar.addContent(sendButton, ComboContent.AnchorSide.RIGHT);

        // Tabbed root view: Chat tab, Online tab, and shared bottom bar
        this.tabView = new TabbedView(this, List.of(chatTab, onlineTab, bottomBar));
    }

    /**
     * Append a new chat message to the Chat tab.
     */
    public void addMessage(String msg) {
        if (messageView == null) return;
        messageView.add(msg, 0xFFFFFF);
    }

    /**
     * Replace the Online tab contents with the current list of online users.
     */
    public void setOnlineUsers(List<String> users) {
        if (onlineView == null) return;

        onlineView.clear();
        onlineView.add("§bOnline users:", 0xA0A0FF);

        for (String user : users) {
            onlineView.add(" - " + user, 0xCCCCCC);
        }
    }

    @Override
    public WindowView getRootView() {
        return tabView;
    }
}
