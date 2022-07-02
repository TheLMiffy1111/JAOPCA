package thelm.jaopca.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IBlockInfo extends IMaterialFormInfo, IBlockProvider, IItemProvider {

	IMaterialFormBlock getMaterialFormBlock();

	IMaterialFormBlockItem getMaterialFormBlockItem();

	@Override
	default Block asBlock() {
		return getMaterialFormBlock().asBlock();
	}

	default ItemBlock asBlockItem() {
		return getMaterialFormBlockItem().asBlockItem();
	}

	@Override
	default Item asItem() {
		return asBlockItem();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormBlock();
	}
}
