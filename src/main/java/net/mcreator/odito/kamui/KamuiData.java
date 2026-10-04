package net.mcreator.odito.kamui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/** Стиль глаз Камуи у игрока. Хранится в NBT, переживает смерть. */
public class KamuiData {

    public static final String EYES_KEY = "kamui_eyes";

    /** 0 — обычные глаза, 1 — Камуи, 2 — Шаринган, 3 — Риннеган. */
    public static final int EYES_NONE = 0;
    public static final int EYES_KAMUI = 1;
    public static final int EYES_SHARINGAN = 2;
    public static final int EYES_RINNEGAN = 3;

    public static int getEyeStyle(Player player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getInt(EYES_KEY);
    }

    public static void setEyeStyle(Player player, int style) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persisted.putInt(EYES_KEY, style);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
}
