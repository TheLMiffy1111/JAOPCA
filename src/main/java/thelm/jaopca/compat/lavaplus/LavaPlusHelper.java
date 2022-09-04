package thelm.jaopca.compat.lavaplus;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.lavaplus.recipes.InductionFurnaceRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class LavaPlusHelper {

	public static final LavaPlusHelper INSTANCE = new LavaPlusHelper();

	private LavaPlusHelper() {}

	public boolean registerInductionFurnaceRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new InductionFurnaceRecipeSerializer(key, input, output, outputCount));
	}
}
