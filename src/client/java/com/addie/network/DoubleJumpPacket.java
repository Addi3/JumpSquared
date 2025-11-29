package com.addie.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DoubleJumpPacket {

    public static final Identifier S2C_ID = new Identifier("jump", "doublejump");

    private static final ConcurrentHashMap<UUID, Boolean> enabledMap = new ConcurrentHashMap<>();

    public static void send(ClientPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(player.getUuid());
        ClientPlayNetworking.send(S2C_ID, buf);
    }

    public static boolean isDoubleJumpEnabled(ClientPlayerEntity player) {
        return enabledMap.getOrDefault(player.getUuid(), false);
    }

    public static void setDoubleJumpEnabled(ClientPlayerEntity player, boolean enabled) {
        enabledMap.put(player.getUuid(), enabled);
    }

    public static void handleSyncPacket(UUID playerUuid, boolean enabled) {
        enabledMap.put(playerUuid, enabled);
    }
}
