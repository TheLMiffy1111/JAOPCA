package thelm.jaopca.fluids;

import java.util.function.Supplier;

import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluidType;

record FluidInfo(Supplier<IMaterialFormFluid> fluid, Supplier<IMaterialFormFluidType> fluidType, Supplier<IMaterialFormFluidBlock> fluidBlock, Supplier<IMaterialFormBucketItem> bucketItem) implements IFluidInfo {

	@Override
	public IMaterialFormFluid getMaterialFormFluid() {
		return fluid.get();
	}

	@Override
	public IMaterialFormFluidType getMaterialFormFluidType() {
		return fluidType.get();
	}

	@Override
	public IMaterialFormFluidBlock getMaterialFormFluidBlock() {
		return fluidBlock.get();
	}

	@Override
	public IMaterialFormBucketItem getMaterialFormBucketItem() {
		return bucketItem.get();
	}
}
