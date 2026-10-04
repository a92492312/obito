/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.odito.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.odito.block.KamuiBlockBlock;
import net.mcreator.odito.block.FekewaterBlock;
import net.mcreator.odito.OditoMod;

public class OditoModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, OditoMod.MODID);
	public static final RegistryObject<Block> KAMUI_BLOCK;
	public static final RegistryObject<Block> FEKEWATER;
	static {
		KAMUI_BLOCK = REGISTRY.register("kamui_block", KamuiBlockBlock::new);
		FEKEWATER = REGISTRY.register("fekewater", FekewaterBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}