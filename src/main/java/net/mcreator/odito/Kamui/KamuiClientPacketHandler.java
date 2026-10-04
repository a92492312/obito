package net.mcreator.odito.kamui;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;

/** Только клиент: применяет полученный стиль к нужному игроку. */
public class KamuiClientPacketHandler {

    public static void handle(UUID uuid, int style) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Player target = level.getPlayerByUUID(uuid);
        if (target != null) {
            KamuiData.setEyeStyle(target, style);
        }
    }
}
