package garlicrot.rusherchat;

import org.rusherhack.client.api.RusherHackAPI;
import java.util.logging.Logger;

/**
 * Main entry-point for the RusherChat plugin.
 *
 * Registers the singleton RusherChatModule on load and logs lifecycle events.
 *
 * @author GarlicRot
 */
public class RusherChatPlugin extends org.rusherhack.client.api.plugin.Plugin {
    private static final Logger LOGGER = Logger.getLogger(RusherChatPlugin.class.getName());

    @Override
    public void onLoad() {
        // Register the singleton RusherChatModule
        RusherHackAPI.getModuleManager().registerFeature(RusherChatModule.getInstance());
        LOGGER.info("RusherChat loaded and module registered!");
    }

    @Override
    public void onUnload() {
        // Disable the module to ensure clean shutdown
        RusherChatModule module = RusherChatModule.getInstance();
        if (module.isToggled()) {
            module.toggle(); // Calls onDisable to close ChatClient and hide window
            LOGGER.info("RusherChat module disabled during unload");
        }
        LOGGER.info("RusherChat unloaded!");
    }
}