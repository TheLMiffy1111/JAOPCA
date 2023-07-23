package thelm.jaopca.api.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IBlockInfo extends IMaterialFormInfo, IBlockLike, ItemLike {

	IMaterialFormBlock getMaterialFormBlock();

	IMaterialFormBlockItem getMaterialFormBlockItem();

	default Block getBlock() {
		return getMaterialFormBlock().toBlock();
	}

	default BlockItem getBlockItem() {
		return getMaterialFormBlockItem().toBlockItem();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormBlock();
	}

	@Override
	default Item asItem() {
		return getBlockItem();
	}

	@Override
	default Block asBlock() {
		return getMaterialFormBlock().toBlock();
	}
}
