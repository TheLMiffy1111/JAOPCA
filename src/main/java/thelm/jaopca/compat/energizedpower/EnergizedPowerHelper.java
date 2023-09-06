package thelm.jaopca.compat.energizedpower;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.energizedpower.recipes.CompressorRecipeSerializer;
import thelm.jaopca.compat.energizedpower.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class EnergizedPowerHelper {

	public static final EnergizedPowerHelper INSTANCE = new EnergizedPowerHelper();

	private EnergizedPowerHelper() {}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, double[] chances, Object secondOutput, double[] secondChances) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, chances, secondOutput, secondChances));
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, double[] chances) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, chances));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeSerializer(key, input, output, count));
	}
}
