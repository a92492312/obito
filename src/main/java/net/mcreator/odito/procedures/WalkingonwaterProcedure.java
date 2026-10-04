package net.mcreator.odito.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

public class WalkingonwaterProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() instanceof LiquidBlock || (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.SEAGRASS
				|| (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.TALL_SEAGRASS) {
			entity.setDeltaMovement(new Vec3((entity.getDeltaMovement().x()), 0, (entity.getDeltaMovement().z())));
		}
	}
}