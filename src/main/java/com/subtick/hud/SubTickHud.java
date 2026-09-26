package com.subtick.hud;

import com.subtick.config.SubTickConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.Queue;

public final class SubTickHud implements HudElement {

    private static final Queue<Long> leftClicks = new LinkedList<>();
    private static final Queue<Long> rightClicks = new LinkedList<>();

    private static final int VALUE_COLOR_LOW = 0x55FF55;
    private static final int VALUE_COLOR_MID = 0xFFFF55;
    private static final int VALUE_COLOR_HIGH = 0xFF5555;
    private static final int BACKGROUND_COLOR = 0x80000000;

    public static void recordLeftClick() {
        leftClicks.add(System.currentTimeMillis());
    }

    public static void recordRightClick() {
        rightClicks.add(System.currentTimeMillis());
    }

    private int getCps(Queue<Long> clicks) {
        long now = System.currentTimeMillis();
        while (!clicks.isEmpty() && now - clicks.peek() > 1000) {
            clicks.poll();
        }
        return clicks.size();
    }

    public void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("subtick-input-engine", "cps_hud"), this);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        SubTickConfig config = SubTickConfig.getInstance();
        if (!config.isHudEnabled()) return;

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) return;
        if (client.getDebugOverlay().showDebugScreen()) return;

        int leftCps = getCps(leftClicks);
        int rightCps = getCps(rightClicks);

        Font font = client.font;
        int x = config.getHudX();
        int y = config.getHudY();

        // Arka plan
        extractor.fill(x, y, x + 60, y + 32, BACKGROUND_COLOR);

        // Başlık
        extractor.textRenderer().accept(x + 20, y + 4, Component.literal("§lCPS"));

        // Sol Tık
        int leftColor = getColorForCps(leftCps);
        extractor.textRenderer().accept(x + 4, y + 18, Component.literal("L: " + leftCps).withStyle(s -> s.withColor(leftColor)));

        // Sağ Tık
        int rightColor = getColorForCps(rightCps);
        String rightText = "R: " + rightCps;
        int rightWidth = font.width(rightText);
        extractor.textRenderer().accept(x + 60 - rightWidth - 4, y + 18, Component.literal(rightText).withStyle(s -> s.withColor(rightColor)));
    }

    private int getColorForCps(int cps) {
        if (cps >= 15) return VALUE_COLOR_HIGH;
        if (cps >= 9) return VALUE_COLOR_MID;
        return VALUE_COLOR_LOW;
    }
}
