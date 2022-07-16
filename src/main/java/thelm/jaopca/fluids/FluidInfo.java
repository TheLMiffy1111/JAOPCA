package thelm.jaopca.fluids;

import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluidType;

record FluidInfo(IMaterialFormFluid fluid, IMaterialFormFluidType fluidType, IMaterialFormFluidBlock fluidBlock, IMaterialFormBucketItem bucketItem) implements IFluidInfo {

	@Override
	public IMaterialFormFluid getMaterialFormFluid() {
		return fluid;
	}

	@Override
	public IMaterialFormFluidType getMaterialFormFluidType() {
		return fluidType;
	}

	@Override
	public IMaterialFormFluidBlock getMaterialFormFluidBlock() {
		return fluidBlock;
	}

	@Override
	public IMaterialFormBucketItem getMaterialFormBucketItem() {
		return bucketItem;
	}
}
