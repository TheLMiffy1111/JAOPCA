package thelm.jaopca.blocks;

import java.util.function.Supplier;

import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;

record BlockInfo(Supplier<IMaterialFormBlock> block, Supplier<IMaterialFormBlockItem> blockItem) implements IBlockInfo {

	@Override
	public IMaterialFormBlock getMaterialFormBlock() {
		return block.get();
	}

	@Override
	public IMaterialFormBlockItem getMaterialFormBlockItem() {
		return blockItem.get();
	}
}
