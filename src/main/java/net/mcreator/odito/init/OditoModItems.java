/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.odito.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.odito.OditoMod;

public class OditoModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, OditoMod.MODID);
	public static final RegistryObject<Item> KAMUI_BLOCK;
	public static final RegistryObject<Item> FEKEWATER;
	static {
		KAMUI_BLOCK = block(OditoModBlocks.KAMUI_BLOCK);
		FEKEWATER = block(OditoModBlocks.FEKEWATER);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return block(block, new Item.Properties());
	}

	private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}