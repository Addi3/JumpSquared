package com.addie.mixin.client;

import com.addie.duck.DoubleJumpDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityMixinClient implements DoubleJumpDuck {

    @Override
    public int jumpSquared$getCooldown() {
        return doubleJumpCooldown;
    }

    @Override
    public int jumpSquared$getCooldownMax() {
        return DOUBLE_JUMP_COOLDOWN_TICKS;
    }
    @Unique private boolean prevJumping = false;
    @Unique private boolean hasLeftGround = false;
    @Unique private int extraJumps = 1;

    @Unique private int doubleJumpCooldown = 0;
    @Unique private final int DOUBLE_JUMP_COOLDOWN_TICKS = 40;

    @Unique private static final double MIDAIR_JUMP_BOOST = 0.9;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickMovement(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        boolean jumping = player.input.jumping;

        if (doubleJumpCooldown > 0) {
            doubleJumpCooldown--;
        }

        if (player.isOnGround()) {
            extraJumps = 1;
            hasLeftGround = false;
        }

        if (!player.isOnGround() && !hasLeftGround && prevJumping) {
            hasLeftGround = true;
        }

        if (doubleJumpCooldown == 0 &&
                hasLeftGround &&
                !player.isOnGround() &&
                jumping && !prevJumping &&
                extraJumps > 0) {

            Vec3d vel = player.getVelocity();
            player.setVelocity(vel.x, MIDAIR_JUMP_BOOST, vel.z);
            player.velocityModified = true;

            player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);

            for (int i = 0; i < 20; i++) {
                double offsetX = (Math.random() - 0.5) * 2.0;
                double offsetY = Math.random() * 0.5;
                double offsetZ = (Math.random() - 0.5) * 2.0;

                player.getWorld().addParticle(
                        ParticleTypes.CLOUD,
                        player.getX() + offsetX,
                        player.getY() + offsetY,
                        player.getZ() + offsetZ,
                        0.0, 0.0, 0.0
                );
            }


            extraJumps--;

            doubleJumpCooldown = DOUBLE_JUMP_COOLDOWN_TICKS;

        }

        prevJumping = jumping;
    }

    public int getJumpCooldown() {
        return doubleJumpCooldown;
    }

    public int getJumpCooldownMax() {
        return DOUBLE_JUMP_COOLDOWN_TICKS;
    }
}
