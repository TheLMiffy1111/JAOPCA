package thelm.jaopca.blocks;

import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;

public class BlockInfo implements IBlockInfo {

	private final BlockMaterialForm block;
	private final MaterialFormBlockItem itemBlock;

	BlockInfo(BlockMaterialForm block, MaterialFormBlockItem itemBlock) {
		this.block = block;
		this.itemBlock = itemBlock;
	}

	@Override
	public BlockMaterialForm getBlock() {
		return block;
	}

	@Override
	public MaterialFormBlockItem getItemBlock() {
		return itemBlock;
	}
}
