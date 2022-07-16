package thelm.jaopca.api.fluids;

public interface IFluidTypeCreator {

	IMaterialFormFluidType create(IMaterialFormFluid fluid, IFluidFormSettings settings);
}
