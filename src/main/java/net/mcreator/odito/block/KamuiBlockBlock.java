package net.mcreator.odito.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class KamuiBlockBlock extends Block {
	public KamuiBlockBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(-1, 3600000).pushReaction(PushReaction.IGNORE).instrument(NoteBlockInstrument.BASEDRUM));
	}
}