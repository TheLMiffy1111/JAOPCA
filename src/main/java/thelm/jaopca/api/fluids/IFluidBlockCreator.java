package thelm.jaopca.api.fluids;

import net.minecraft.resources.Identifier;

public interface IFluidBlockCreator {

	IMaterialFormFluidBlock create(IMaterialFormFluid fluid, IFluidFormSettings settings, Identifier registryName);
}
