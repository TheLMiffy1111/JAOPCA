package thelm.jaopca.fluids;

import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;

class FluidInfo implements IFluidInfo {

	private final IMaterialFormFluid fluid;
	private final IMaterialFormFluidBlock fluidBlock;

	FluidInfo(IMaterialFormFluid fluid, IMaterialFormFluidBlock fluidBlock) {
		this.fluid = fluid;
		this.fluidBlock = fluidBlock;
	}

	@Override
	public IMaterialFormFluid getMaterialFormFluid() {
		return fluid;
	}

	@Override
	public IMaterialFormFluidBlock getMaterialFormFluidBlock() {
		return fluidBlock;
	}
}
