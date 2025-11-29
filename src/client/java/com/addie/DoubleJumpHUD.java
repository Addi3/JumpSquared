package com.addie;

import com.addie.duck.DoubleJumpDuck;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DoubleJumpHUD implements HudRenderCallback {

    public static final Identifier ICON =
            new Identifier("jumpsquared", "textures/gui/double_jump_bar.png");

    private static final int BAR_WIDTH = 41;
    private static final int BAR_HEIGHT = 5;

    private int yOffset = 45;
    private int xOffset = -70;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        DoubleJumpDuck duck = (DoubleJumpDuck) mc.player;

        int cooldown = duck.jumpSquared$getCooldown();
        int max = duck.jumpSquared$getCooldownMax();
        if (max <= 0) max = 1;

        float pct = 1f - ((float) cooldown / max);
        int fillWidth = (int) (BAR_WIDTH * pct);

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        int xPos = (screenWidth - BAR_WIDTH) / 2 + xOffset;
        int yPos = screenHeight - yOffset;
        if (mc.player.isCreative() || mc.player.isSpectator()) {
            yPos = screenHeight - 35;
        }
        drawContext.drawTexture(ICON, xPos, yPos, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT * 2);

        if (fillWidth > 0) {
            drawContext.drawTexture(ICON, xPos, yPos, 0, BAR_HEIGHT, fillWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT * 2);
        }
    }

    public void setYOffset(int yOffset) { this.yOffset = yOffset; }
    public void setXOffset(int xOffset) { this.xOffset = xOffset; }
}
