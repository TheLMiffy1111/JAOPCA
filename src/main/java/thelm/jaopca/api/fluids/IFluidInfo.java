package thelm.jaopca.api.fluids;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IFluidInfo extends IMaterialFormInfo<MaterialFormFluid>, IItemProvider {

	MaterialFormFluid getFluid();

	BucketItem getBucketItem();

	@Override
	default MaterialFormFluid getMaterialForm() {
		return getFluid();
	}

	@Override
	default Item asItem() {
		return getBucketItem();
	}
}
