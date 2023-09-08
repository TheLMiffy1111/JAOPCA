package thelm.jaopca.api.blocks;

import net.minecraft.world.item.BlockItem;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlockItem extends IMaterialForm {

	default BlockItem toBlockItem() {
		return (BlockItem)this;
	}
}
