package thelm.jaopca.api.fluids;

import net.minecraft.world.item.Item;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBucketItem extends IMaterialForm {

	default Item asItem() {
		return (Item)this;
	}
}
