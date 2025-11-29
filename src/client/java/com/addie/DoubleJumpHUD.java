package com.addie;

import com.addie.duck.DoubleJumpDuck;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DoubleJumpHUD implements HudRenderCallback {

    private static final Identifier COOLDOWN_ICON =
            new Identifier("jumpsquared", "textures/gui/double_jump_bar.png");

    private static final Identifier CHARGE_ICON =
            new Identifier("jumpsquared", "textures/gui/crouch_charge_bar.png");

    private static final int BAR_WIDTH = 41;
    private static final int BAR_HEIGHT = 5;

    private int yOffset = 45;
    private int xOffset = 30;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        DoubleJumpDuck duck = (DoubleJumpDuck) mc.player;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int xPos = (screenWidth - BAR_WIDTH) / 2 + xOffset;
        int yPos = screenHeight - yOffset;

        if (mc.player.isCreative() || mc.player.isSpectator()) {
            yPos = screenHeight - 35;
        }

        int cooldown = duck.jumpSquared$getCooldown();
        int cooldownMax = duck.jumpSquared$getCooldownMax();
        if (cooldownMax <= 0) cooldownMax = 1;

        float cdPct = 1f - ((float) cooldown / cooldownMax);
        int cdFillWidth = (int) (BAR_WIDTH * cdPct);

        drawContext.drawTexture(
                COOLDOWN_ICON,
                xPos, yPos,
                0, 0,
                BAR_WIDTH, BAR_HEIGHT,
                BAR_WIDTH, BAR_HEIGHT * 2
        );

        if (cdFillWidth > 0) {
            drawContext.drawTexture(
                    COOLDOWN_ICON,
                    xPos, yPos,
                    0, BAR_HEIGHT,
                    cdFillWidth, BAR_HEIGHT,
                    BAR_WIDTH, BAR_HEIGHT * 2
            );
        }

        int charge = duck.jumpSquared$getCrouchCharge();
        int chargeMax = duck.jumpSquared$getCrouchChargeMax();
        if (chargeMax <= 0) chargeMax = 1;

        float chargePct = Math.min(1f, (float) charge / chargeMax);
        int chargeFillWidth = (int) (BAR_WIDTH * chargePct);

        int chargeX = xPos + 45;
        int chargeY = yPos;

        drawContext.drawTexture(
                CHARGE_ICON,
                chargeX, chargeY,
                0, 0,
                BAR_WIDTH, BAR_HEIGHT,
                BAR_WIDTH, BAR_HEIGHT * 2
        );

        if (chargeFillWidth > 0) {
            drawContext.drawTexture(
                    CHARGE_ICON,
                    chargeX, chargeY,
                    0, BAR_HEIGHT,
                    chargeFillWidth, BAR_HEIGHT,
                    BAR_WIDTH, BAR_HEIGHT * 2
            );
        }
    }

    public void setYOffset(int yOffset) { this.yOffset = yOffset; }
    public void setXOffset(int xOffset) { this.xOffset = xOffset; }
}
