package net.mcreator.odito.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.odito.init.OditoModBlocks;

public class WaterSurfingOnEffectActiveTickProcedureProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.WATER) {
			world.setBlock(BlockPos.containing(x, y - 1, z), OditoModBlocks.FEKEWATER.get().defaultBlockState(), 3);
		}
	}
}