package thelm.jaopca.api.fluids;

import net.minecraft.resources.Identifier;

public interface IBucketItemCreator {

	IMaterialFormBucketItem create(IMaterialFormFluid fluid, IFluidFormSettings settings, Identifier registryName);
}
