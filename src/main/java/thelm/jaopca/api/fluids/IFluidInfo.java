package thelm.jaopca.api.fluids;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IFluidInfo extends IMaterialFormInfo<FluidMaterialForm>, IItemProvider {

	FluidMaterialForm getFluid();

	ItemBucket getItemBucket();

	@Override
	default FluidMaterialForm getMaterialForm() {
		return getFluid();
	}

	@Override
	default Item asItem() {
		return getItemBucket();
	}
}
