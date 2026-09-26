package com.subtick;

import com.subtick.config.SubTickConfig;
import com.subtick.hud.SubTickHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubTickInputEngine implements ClientModInitializer {

    public static final String MOD_ID = "subtick-input-engine";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        SubTickConfig config = SubTickConfig.getInstance();

        LOGGER.info("╔══════════════════════════════════════════════════╗");
        LOGGER.info("║     SubTick Input Engine v1.0.0 — Loaded        ║");
        LOGGER.info("║  Frame-aligned input capture system active.     ║");
        LOGGER.info("║  HUD: {}                                       ║",
            config.isHudEnabled() ? "ON " : "OFF");
        LOGGER.info("╚══════════════════════════════════════════════════╝");

        // HUD kaydı
        SubTickHud hud = new SubTickHud();
        hud.register();

        // Tuş ataması
        KeyMapping toggleHudKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.subtick.toggle_hud",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.MISC
        ));

        // Tick sırasında tuşlara basılıp basılmadığını kontrol et
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.consumeClick()) {
                boolean newState = config.toggleHud();
                config.save();
                if (client.player != null) {
                    client.player.sendOverlayMessage(
                        Component.literal("§a[ClickOptimizer] §fCPS HUD " + (newState ? "§aON" : "§cOFF"))
                    );
                }
            }
        });
    }
}
