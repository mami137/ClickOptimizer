package com.subtick;

import com.subtick.config.SubTickConfig;

import com.subtick.hud.SubTickHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubTickInputEngine implements ClientModInitializer {

    public static final String MOD_ID = "subtick-input-engine";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static SubTickConfig config;
    private static boolean debugEnabled = false;



    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static SubTickConfig getConfig() {
        return config;
    }

    @Override
    public void onInitializeClient() {
        // Config yükle
        config = SubTickConfig.load();
        debugEnabled = config.isDebugMode();

        LOGGER.info("╔══════════════════════════════════════════════════╗");
        LOGGER.info("║     SubTick Input Engine v1.0.0 — Loaded        ║");
        LOGGER.info("║  Frame-aligned input capture system active.     ║");
        LOGGER.info("║  Debug: {} | HUD: {} | Keyboard: {}           ║",
            debugEnabled ? "ON " : "OFF",
            config.isHudEnabled() ? "ON " : "OFF",
            config.isCaptureKeyboard() ? "ON " : "OFF");
        LOGGER.info("╚══════════════════════════════════════════════════╝");

        // HUD kaydı
        SubTickHud hud = new SubTickHud();
        hud.register();

        // Tuş ataması (KeyBinding) HUD açıp kapatmak için (Varsayılan: 'H' tuşu)
        KeyBinding toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.subtick.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "key.categories.misc"
        ));

        // Tick sırasında tuşlara basılıp basılmadığını kontrol et
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                boolean newState = config.toggleHud();
                config.save();
                if (client.player != null) {
                    client.player.sendMessage(
                        net.minecraft.text.Text.literal("§a[Sub-Tick] §fCPS HUD " + (newState ? "§aAçık" : "§cKapalı")), 
                        true
                    );
                }
            }
        });
    }
}
