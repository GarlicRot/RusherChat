package garlicrot.rusherchat;

import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.RichTextView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.util.List;
import java.util.function.Consumer;

public class RusherChatWindow extends ResizeableWindow {

    private final RichTextView messageView;
    private final TabbedView tabView;

    public RusherChatWindow(ChatClient client, Consumer<String> onSend) {
        super("RusherChat", 100, 100, 600, 250);

        // Message display area
        this.messageView = new RichTextView("Messages", this);

        // Input + Send button
        final TextFieldComponent inputField = new TextFieldComponent(this, "Enter message...", 100);
        final ButtonComponent sendButton = new ButtonComponent(this, "Send", () -> {
            String input = inputField.getValue().trim();
            if (!input.isEmpty()) {
                onSend.accept(input);
                addMessage("§7[You] " + input);
                inputField.setValue("");
            }
        });

        inputField.setReturnCallback(str -> sendButton.onClick());

        final ComboContent bottomBar = new ComboContent(this);
        bottomBar.addContent(inputField, ComboContent.AnchorSide.LEFT);
        bottomBar.addContent(sendButton, ComboContent.AnchorSide.RIGHT);

        this.tabView = new TabbedView(this, List.of(messageView, bottomBar));

        this.setMinWidth(300);
        this.setMinHeight(200);
    }

    public void addMessage(String msg) {
        if (messageView == null) return;
        messageView.add(msg, 0xFFFFFF); // Color value is fallback for plain text
    }

    @Override
    public WindowView getRootView() {
        return tabView; // Return null if tabView is null
    }
}