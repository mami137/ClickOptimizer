package com.subtick.hud;

import com.subtick.config.SubTickConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.LinkedList;
import java.util.Queue;

public final class SubTickHud implements HudRenderCallback {

    private static final Queue<Long> leftClicks = new LinkedList<>();
    private static final Queue<Long> rightClicks = new LinkedList<>();

    private static final int BG_COLOR = 0x90000000;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int CPS_LOW_COLOR = 0xFF55FF55;
    private static final int CPS_MED_COLOR = 0xFFFFFF55;
    private static final int CPS_HIGH_COLOR = 0xFFFF5555;
    private static final int LABEL_COLOR = 0xFFAAAAAA;

    public static void recordLeftClick() {
        synchronized (leftClicks) {
            leftClicks.add(System.currentTimeMillis());
        }
    }

    public static void recordRightClick() {
        synchronized (rightClicks) {
            rightClicks.add(System.currentTimeMillis());
        }
    }

    private static int getCPS(Queue<Long> clicks) {
        long now = System.currentTimeMillis();
        synchronized (clicks) {
            while (!clicks.isEmpty() && (now - clicks.peek()) > 1000) {
                clicks.poll();
            }
            return clicks.size();
        }
    }

    public void register() {
        HudRenderCallback.EVENT.register(this);
    }

    @Override
    public void onHudRender(DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickCounter) {
        SubTickConfig config = SubTickConfig.getInstance();
        if (!config.isHudEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        if (client.getDebugHud().shouldShowDebugHud()) return;

        TextRenderer textRenderer = client.textRenderer;

        int leftCPS = getCPS(leftClicks);
        int rightCPS = getCPS(rightClicks);

        int x = config.getHudX();
        int y = config.getHudY();
        int padding = 5;
        int lineHeight = 12;

        String leftText = "L: " + leftCPS + " CPS";
        String rightText = "R: " + rightCPS + " CPS";
        String title = "CPS Counter";

        int maxTextWidth = Math.max(
            Math.max(textRenderer.getWidth(leftText), textRenderer.getWidth(rightText)),
            textRenderer.getWidth(title)
        );
        int boxWidth = maxTextWidth + (padding * 2) + 4;
        int boxHeight = (lineHeight * 3) + (padding * 2) + 2;

        drawContext.fill(x, y, x + boxWidth, y + boxHeight, BG_COLOR);
        drawContext.fill(x, y, x + boxWidth, y + 1, 0xFF44AAFF);

        int textX = x + padding + 2;
        int textY = y + padding + 2;

        drawContext.drawText(textRenderer, "§f§lCPS", textX, textY, TITLE_COLOR, true);
        textY += lineHeight + 2;

        String leftColorCode = leftCPS <= 5 ? "§a" : leftCPS <= 12 ? "§e" : "§c";
        drawContext.drawText(textRenderer,
            "§7L: " + leftColorCode + leftCPS,
            textX, textY, LABEL_COLOR, true);
        textY += lineHeight;

        String rightColorCode = rightCPS <= 5 ? "§a" : rightCPS <= 12 ? "§e" : "§c";
        drawContext.drawText(textRenderer,
            "§7R: " + rightColorCode + rightCPS,
            textX, textY, LABEL_COLOR, true);
    }
}
