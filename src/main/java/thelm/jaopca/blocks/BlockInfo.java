package thelm.jaopca.blocks;

import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;

record BlockInfo(IMaterialFormBlock block, IMaterialFormBlockItem blockItem) implements IBlockInfo {

	@Override
	public IMaterialFormBlock getMaterialFormBlock() {
		return block;
	}

	@Override
	public IMaterialFormBlockItem getMaterialFormBlockItem() {
		return blockItem;
	}
}
