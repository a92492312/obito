package net.mcreator.odito.mixin;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.core.Holder;

import net.mcreator.odito.init.OditoModBiomes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements OditoModBiomes.OditoModNoiseGeneratorSettings {
	@Unique
	private Holder<DimensionType> odito_dimensionTypeReference;

	@WrapMethod(method = "surfaceRule")
	public SurfaceRules.RuleSource surfaceRule(Operation<SurfaceRules.RuleSource> original) {
		SurfaceRules.RuleSource retval = original.call();
		if (this.odito_dimensionTypeReference != null) {
			retval = OditoModBiomes.adaptSurfaceRule(retval, this.odito_dimensionTypeReference);
		}
		return retval;
	}

	@Override
	public void setoditoDimensionTypeReference(Holder<DimensionType> dimensionType) {
		this.odito_dimensionTypeReference = dimensionType;
	}
}