package com.addie.duck;

/**
 * Accessor interface for jump/dash UI and state.
 * Implemented by the PlayerEntity mixin on the client player.
 */
public interface DoubleJumpDuck {
    // existing double-jump cooldown accessors
    int jumpSquared$getCooldown();
    int jumpSquared$getCooldownMax();

    // crouch-charge accessors (new)
    int jumpSquared$getCrouchCharge();
    int jumpSquared$getCrouchChargeMax();
}
