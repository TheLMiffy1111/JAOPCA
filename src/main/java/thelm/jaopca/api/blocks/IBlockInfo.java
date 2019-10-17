package thelm.jaopca.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IBlockInfo extends IMaterialFormInfo, IBlockProvider, IItemProvider {

	IMaterialFormBlock getMaterialFormBlock();

	IMaterialFormBlockItem getMaterialFormBlockItem();

	default Block getBlock() {
		return getMaterialFormBlock().asBlock();
	}

	default BlockItem getBlockItem() {
		return getMaterialFormBlockItem().asBlockItem();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormBlock();
	}

	@Override
	default Block asBlock() {
		return getBlock();
	}

	@Override
	default Item asItem() {
		return getBlockItem();
	}
}
