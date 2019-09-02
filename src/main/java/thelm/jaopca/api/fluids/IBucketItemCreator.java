package thelm.jaopca.api.fluids;

public interface IBucketItemCreator {

	IMaterialFormBucketItem create(IMaterialFormFluid fluid, IFluidFormSettings settings);
}
