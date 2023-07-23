package thelm.jaopca.api.items;

import net.minecraft.world.item.Item;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormItem extends IMaterialForm {

	default Item toItem() {
		return (Item)this;
	}
}
