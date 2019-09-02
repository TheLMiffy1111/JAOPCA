package thelm.jaopca.api.fluids;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IFluidInfo extends IMaterialFormInfo, IItemProvider {

	IMaterialFormFluid getMaterialFormFluid();

	IMaterialFormFluidBlock getMaterialFormFluidBlock();

	IMaterialFormBucketItem getMaterialFormBucketItem();

	default Fluid getFluid() {
		return getMaterialFormFluid().asFluid();
	}

	default Block getFluidBlock() {
		return getMaterialFormFluidBlock().asBlock();
	}

	default Item getBucketItem() {
		return getMaterialFormBucketItem().asItem();
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
