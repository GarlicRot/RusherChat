package org.rusherchat;

import org.rusherchat.windows.ChatWindow;
import org.rusherhack.client.api.RusherHackAPI;

/**
 * Chat plugin for RusherHacks using the Rusher window API
 */
public class RusherChatPlugin extends org.rusherhack.client.api.plugin.Plugin {

    @Override
    public void onLoad() {
        this.getLogger().info("Chat plugin loaded!");

        final ChatWindow chatWindow = new ChatWindow();

        // Register the chat window with the RusherHacks API
        RusherHackAPI.getWindowManager().registerFeature(chatWindow);
    }

    @Override
    public void onUnload() {
        this.getLogger().info("Chat plugin unloaded!");
    }
}
