package net.mcreator.odito.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KamuiKeyBindings {
    public static KeyMapping kamuiTeleportKey;

    public static void init() {
        kamuiTeleportKey = new KeyMapping(
                "key.odito.kamui_teleport.desc",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "category.odito"
        );
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (kamuiTeleportKey != null && kamuiTeleportKey.isDown()) {
            if (minecraft.player != null && !minecraft.level.isClientSide) {
                // логика телепортации
            }
        }
    }
}
