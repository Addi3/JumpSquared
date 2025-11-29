package com.addie.mixin.client;

import com.addie.duck.DoubleJumpDuck;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityMixinClient implements DoubleJumpDuck {

    @Unique private boolean prevJumping = false;
    @Unique private boolean hasLeftGround = false;
    @Unique private int extraJumps = 1;

    @Unique private int doubleJumpCooldown = 0;
    @Unique private static final int DOUBLE_JUMP_COOLDOWN_TICKS = 30;
    @Unique private static final double MIDAIR_JUMP_BOOST = 1.0;
    @Unique private static final double MIDAIR_FORWARD_BOOST = 0.9;

    @Unique private int crouchTicks = 0;
    @Unique private static final int CROUCH_CHARGE_TICKS = 40;
    @Unique private boolean fullyCharged = false;
    @Unique private static final double BOOST_JUMP_VELOCITY = 2.0;
    @Unique private boolean isBoosting = false;
    @Unique private int boostingTicks = 0;
    @Unique private static final int BOOST_DURATION_TICKS = 20;

    @Unique private static final int BOOST_READY_DELAY_TICKS = 10;
    @Unique private int boostReadyCounter = 0;

    @Unique private static final float HUNGER_COST_DOUBLE = 2.5f;
    @Unique private static final float HUNGER_COST_BOOST = 4f;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void preTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        boolean jumping = player.input.jumping;
        boolean sneaking = player.input.sneaking;

        if (doubleJumpCooldown > 0) doubleJumpCooldown--;

        if (player.isOnGround()) {
            hasLeftGround = false;
            extraJumps = 1;

            if (!fullyCharged) {
                if (sneaking) {
                    crouchTicks++;
                    if (crouchTicks >= CROUCH_CHARGE_TICKS) {
                        crouchTicks = CROUCH_CHARGE_TICKS;
                        fullyCharged = true;
                        boostReadyCounter = BOOST_READY_DELAY_TICKS;
                    }
                } else {
                    crouchTicks = 0;
                }
            }

            if (fullyCharged && boostReadyCounter > 0) boostReadyCounter--;
        } else {
            if (!hasLeftGround && prevJumping) hasLeftGround = true;
        }

        prevJumping = jumping;

        if (isBoosting) {
            boostingTicks--;
            if (boostingTicks <= 0) isBoosting = false;
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void applyBoostAndMidair(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        boolean jumping = player.input.jumping;
        boolean sneaking = player.input.sneaking;

        if (fullyCharged && boostReadyCounter == 0 && sneaking && !isBoosting && player.isOnGround()) {
            isBoosting = true;
            boostingTicks = BOOST_DURATION_TICKS;
            Vec3d vel = player.getVelocity();
            player.setVelocity(vel.x, BOOST_JUMP_VELOCITY, vel.z);
            player.velocityModified = true;

            ClientWorld world = (ClientWorld) player.getWorld();
            player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
            for (int i = 0; i < 20; i++) {
                double ox = (Math.random() - 0.5) * 2.0;
                double oy = Math.random() * 0.5;
                double oz = (Math.random() - 0.5) * 2.0;
                world.addParticle(ParticleTypes.CLOUD, player.getX() + ox, player.getY() + oy, player.getZ() + oz, 0, 0, 0);
            }

            crouchTicks = 0;
            fullyCharged = false;

            if (!player.getAbilities().creativeMode) player.getHungerManager().addExhaustion(HUNGER_COST_BOOST);
        }

        if (doubleJumpCooldown == 0 &&
                hasLeftGround &&
                !player.isOnGround() &&
                jumping && !prevJumping &&
                extraJumps > 0) {

            float yawRad = (float) Math.toRadians(player.getYaw());
            double forwardX = -Math.sin(yawRad) * MIDAIR_FORWARD_BOOST;
            double forwardZ = Math.cos(yawRad) * MIDAIR_FORWARD_BOOST;

            Vec3d vel = player.getVelocity();
            player.setVelocity(vel.x + forwardX, vel.y + MIDAIR_JUMP_BOOST, vel.z + forwardZ);
            player.velocityModified = true;

            ClientWorld world = (ClientWorld) player.getWorld();
            player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
            for (int i = 0; i < 20; i++) {
                double ox = (Math.random() - 0.5) * 2.0;
                double oy = Math.random() * 0.5;
                double oz = (Math.random() - 0.5) * 2.0;
                world.addParticle(ParticleTypes.CLOUD, player.getX() + ox, player.getY() + oy, player.getZ() + oz, 0, 0, 0);
            }

            extraJumps--;
            doubleJumpCooldown = DOUBLE_JUMP_COOLDOWN_TICKS;

            if (!player.getAbilities().creativeMode) player.getHungerManager().addExhaustion(HUNGER_COST_DOUBLE);
        }
    }

    @Override
    public int jumpSquared$getCooldown() { return doubleJumpCooldown; }

    @Override
    public int jumpSquared$getCooldownMax() { return DOUBLE_JUMP_COOLDOWN_TICKS; }

    @Override
    public int jumpSquared$getCrouchCharge() { return crouchTicks; }

    @Override
    public int jumpSquared$getCrouchChargeMax() { return CROUCH_CHARGE_TICKS; }
}
