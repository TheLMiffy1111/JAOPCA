package thelm.jaopca.api.blocks;

import net.minecraft.item.BlockItem;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlockItem extends IMaterialForm {

	default BlockItem asBlockItem() {
		return (BlockItem)this;
	}
}
