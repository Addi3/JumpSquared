package com.addie;

import com.addie.network.DoubleJumpPacket;
import net.fabricmc.api.ClientModInitializer;
import java.util.UUID;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class JumpSquaredClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DoubleJumpPacket.S2C_ID, (client, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();
            client.execute(() -> {
                PlayerEntity player = client.world.getPlayerByUuid(playerUuid);
                if (player != null) {
                }
            });
        });
        HudRenderCallback.EVENT.register(new DoubleJumpHUD());
    }


}
