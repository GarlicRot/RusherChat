package garlicrot.rusherchat;

import net.minecraft.client.Minecraft;
import org.rusherhack.client.api.RusherHackAPI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
        // Register the singleton RusherChatModule immediately,
        // but delay startup until RusherHack's UI/window system has finished loading.
        RusherChatModule module = RusherChatModule.getInstance();
        RusherHackAPI.getModuleManager().registerFeature(module);

        CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() ->
                Minecraft.getInstance().execute(() -> {
                    module.allowStartup();

                    if (!module.isToggled()) {
                        module.toggle(); // Calls onEnable/startRusherChat once.
                    } else {
                        module.startRusherChat();
                    }

                    LOGGER.fine("RusherChat startup completed after UI delay");
                })
        );

        LOGGER.fine("RusherChat loaded and module registered; startup scheduled.");
    }

    @Override
    public void onUnload() {
        RusherChatModule.getInstance().shutdownRusherChat();
        LOGGER.fine("RusherChat unloaded!");
    }
}