package thelm.jaopca.api.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IFluidInfo extends IMaterialFormInfo, IItemProvider {

	IMaterialFormFluid getMaterialFormFluid();

	IMaterialFormBucketItem geMaterialFormtBucketItem();

	default Fluid getFluid() {
		return getMaterialFormFluid().asFluid();
	}

	default BucketItem getBucketItem() {
		return geMaterialFormtBucketItem().asBucketItem();
	}

	@Override
	default IMaterialFormFluid getMaterialForm() {
		return getMaterialFormFluid();
	}

	@Override
	default Item asItem() {
		return getBucketItem();
	}
}
