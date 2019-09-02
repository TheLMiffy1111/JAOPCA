package thelm.jaopca.api.fluids;

import net.minecraftforge.fluids.FluidAttributes;

public interface IFluidAttributesCreator {

	FluidAttributes create(IMaterialFormFluid fluid, IFluidFormSettings settings);
}
