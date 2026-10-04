/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.odito.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.mcreator.odito.potion.WaterSurfingMobEffect;
import net.mcreator.odito.potion.ObitoEffectMobEffect;
import net.mcreator.odito.OditoMod;

public class OditoModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OditoMod.MODID);
	public static final RegistryObject<MobEffect> WATER_SURFING = REGISTRY.register("water_surfing", WaterSurfingMobEffect::new);
	public static final RegistryObject<MobEffect> OBITO_EFFECT = REGISTRY.register("obito_effect", ObitoEffectMobEffect::new);
}