package thelm.jaopca.api.fluids;

import net.minecraft.item.BucketItem;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBucketItem extends IMaterialForm {

	default BucketItem asBucketItem() {
		return (BucketItem)this;
	}
}
