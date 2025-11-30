package com.addie;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class JumpModKeybind implements ClientModInitializer {

    public static boolean ENABLED = true;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpmod.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.jumpmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                ENABLED = !ENABLED;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("Jump Mod: ")
                                    .append(Text.literal(ENABLED ? "Enabled" : "Disabled")
                                            .styled(style -> style.withColor(ENABLED ? 0x55FF55 : 0xFF5555))),
                            true
                    );
                }
            }
        });
    }
}
