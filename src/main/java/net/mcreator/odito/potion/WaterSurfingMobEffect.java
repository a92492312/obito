package net.mcreator.odito.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.odito.procedures.WaterSurfingOnEffectActiveTickProcedureProcedure;

public class WaterSurfingMobEffect extends MobEffect {
	public WaterSurfingMobEffect() {
		super(MobEffectCategory.NEUTRAL, -1);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		WaterSurfingOnEffectActiveTickProcedureProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}