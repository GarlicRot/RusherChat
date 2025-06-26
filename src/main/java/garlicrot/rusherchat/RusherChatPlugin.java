package garlicrot.rusherchat;

import org.rusherhack.client.api.RusherHackAPI;

/**
 * Main entry-point for the RusherChat plugin.
 *
 * Registers the chat module on load and logs basic lifecycle events.
 *
 * @author GarlicRot
 */
public class RusherChatPlugin extends org.rusherhack.client.api.plugin.Plugin {

    @Override
    public void onLoad() {
        // Register the chat module so users can toggle it in-game
        RusherHackAPI.getModuleManager().registerFeature(new RusherChatModule());
        this.getLogger().info("RusherChat loaded!");
    }

    @Override
    public void onUnload() {
        this.getLogger().info("RusherChat unloaded!");
    }
}
