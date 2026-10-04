package net.mcreator.odito.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PhasingMobEffect extends MobEffect {
    public PhasingMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B00FF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
