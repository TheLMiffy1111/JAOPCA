package thelm.jaopca.compat.energizedpower;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.energizedpower.recipes.CompressorRecipeSerializer;
import thelm.jaopca.compat.energizedpower.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class EnergizedPowerHelper {

	public static final EnergizedPowerHelper INSTANCE = new EnergizedPowerHelper();

	private EnergizedPowerHelper() {}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, double[] chances, double[] chancesAdvanced, Object secondOutput, double[] secondChances, double[] secondChancesAdvanced) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, chances, chancesAdvanced, secondOutput, secondChances, secondChancesAdvanced));
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, double[] chances, double[] chancesAdvanced) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, chances, chancesAdvanced));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeSerializer(key, input, inputCount, output, outputCount));
	}
}
