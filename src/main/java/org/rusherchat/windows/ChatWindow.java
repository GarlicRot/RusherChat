package org.rusherchat.windows;

import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.RichTextView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rusherchat.network.ChatClient;

import java.util.List;


public class ChatWindow extends ResizeableWindow {

    private static final Logger LOGGER = LogManager.getLogger(ChatWindow.class);

    private ChatClient chatClient;
    private final RichTextView messageView;
    private TabbedView rootView;

    public ChatWindow() {
        super("Chat", 150, 100, 300, 300);
        RusherHackAPI.getEventBus().subscribe(this);

        this.messageView = new RichTextView("Messages", this);

        // Attempt to retrieve the player's username
        String username = getMinecraftUsername();

        if (username == null) {
            LOGGER.error("Unable to retrieve username; ChatClient cannot be created.");
            return;
        }

        // Initialize the chat client with the retrieved username
        this.chatClient = new ChatClient("localhost", 12345, username, this);
        try {
            this.chatClient.connect();
        } catch (Exception e) {
            LOGGER.error("Failed to connect to chat server", e);
        }

        final ComboContent inputCombo = new ComboContent(this);

        final TextFieldComponent rawMessage = new TextFieldComponent(this, "Enter message", 100);
        final ButtonComponent sendButton = new ButtonComponent(this, "Send", () -> {
            final String input = rawMessage.getValue();

            LOGGER.info("Send button clicked with message: {}", input);

            if (input.isEmpty()) {
                return;
            }

            // Send the message to the server
            chatClient.sendMessage(input);

            rawMessage.setValue("");
        });

        rawMessage.setReturnCallback((str) -> {
            LOGGER.info("Enter key pressed with message: {}", str);
            sendButton.onClick();
        });

        inputCombo.addContent(rawMessage, ComboContent.AnchorSide.LEFT);
        inputCombo.addContent(sendButton, ComboContent.AnchorSide.RIGHT);

        // Use a TabbedView for the root view
        this.rootView = new TabbedView(this, List.of(this.messageView, inputCombo));
    }

    private String getMinecraftUsername() {
        try {
            if (Globals.mc != null) {
                return Globals.mc.getUser().getName();  // Retrieves the session username
            }
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve username", e);
        }
        return null;
    }

    public void displayMessage(String message) {
        // Simply display the message received from the server without modifying it
        this.messageView.add(message, 0xFFFFFF); // White text for the message
    }

    @Override
    public WindowView getRootView() {
        return this.rootView;
    }

    public void cleanup() {
        try {
            if (chatClient != null) {
                chatClient.disconnect();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to disconnect from chat server", e);
        }
    }
}
