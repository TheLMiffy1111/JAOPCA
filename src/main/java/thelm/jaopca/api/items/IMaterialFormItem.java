package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormItem extends IMaterialForm {

	default Item asItem() {
		return (Item)this;
	}
}
