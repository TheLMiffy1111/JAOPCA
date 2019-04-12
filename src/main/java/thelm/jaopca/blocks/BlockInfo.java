package thelm.jaopca.blocks;

import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.ItemBlockMaterialForm;

public class BlockInfo implements IBlockInfo {

	private final BlockMaterialForm block;
	private final ItemBlockMaterialForm itemBlock;

	BlockInfo(BlockMaterialForm block, ItemBlockMaterialForm itemBlock) {
		this.block = block;
		this.itemBlock = itemBlock;
	}

	@Override
	public BlockMaterialForm getBlock() {
		return block;
	}

	@Override
	public ItemBlockMaterialForm getItemBlock() {
		return itemBlock;
	}
}
