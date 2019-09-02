package thelm.jaopca.fluids;

import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;

public class FluidInfo implements IFluidInfo {

	private final IMaterialFormFluid fluid;
	private final IMaterialFormFluidBlock fluidBlock;
	private final IMaterialFormBucketItem bucketItem;

	FluidInfo(IMaterialFormFluid fluid, IMaterialFormFluidBlock fluidBlock, IMaterialFormBucketItem bucketItem) {
		this.fluid = fluid;
		this.fluidBlock = fluidBlock;
		this.bucketItem = bucketItem;
	}

	@Override
	public IMaterialFormFluid getMaterialFormFluid() {
		return fluid;
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
