package thelm.jaopca.blocks;

import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;

class BlockInfo implements IBlockInfo {

	private final IMaterialFormBlock block;
	private final IMaterialFormBlockItem blockItem;

	BlockInfo(IMaterialFormBlock block, IMaterialFormBlockItem blockItem) {
		this.block = block;
		this.blockItem = blockItem;
	}

	@Override
	public IMaterialFormBlock getMaterialFormBlock() {
		return block;
	}

	@Override
	public IMaterialFormBlockItem getMaterialFormBlockItem() {
		return blockItem;
	}
}
