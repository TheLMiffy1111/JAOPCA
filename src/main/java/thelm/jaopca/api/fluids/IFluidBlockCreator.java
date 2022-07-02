package thelm.jaopca.api.fluids;

public interface IFluidBlockCreator {

	IMaterialFormFluidBlock create(IMaterialFormFluid fluid, IFluidFormSettings settings);
}
