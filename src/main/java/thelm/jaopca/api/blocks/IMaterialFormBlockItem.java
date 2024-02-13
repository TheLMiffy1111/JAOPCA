package thelm.jaopca.api.blocks;

import net.minecraft.item.ItemBlock;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlockItem extends IMaterialForm {

	default ItemBlock toBlockItem() {
		return (ItemBlock)this;
	}
}
