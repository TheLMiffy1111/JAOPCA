package thelm.jaopca.api.blocks;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IBlockInfo extends IMaterialFormInfo<BlockMaterialForm>, IItemProvider {

	BlockMaterialForm getBlock();

	MaterialFormBlockItem getItemBlock();

	@Override
	default BlockMaterialForm getMaterialForm() {
		return getBlock();
	}

	@Override
	default Item asItem() {
		return getItemBlock();
	}
}
