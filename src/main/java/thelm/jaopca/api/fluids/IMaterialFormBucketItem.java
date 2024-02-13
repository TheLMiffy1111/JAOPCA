package thelm.jaopca.api.fluids;

import net.minecraft.item.Item;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBucketItem extends IMaterialForm {

	default Item toItem() {
		return (Item)this;
	}
}
