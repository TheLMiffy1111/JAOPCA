package thelm.jaopca.compat.energizedpower;

import net.minecraft.resources.Identifier;
import thelm.jaopca.compat.energizedpower.recipes.AlloyFurnaceRecipeSerializer;
import thelm.jaopca.compat.energizedpower.recipes.CompressorRecipeSerializer;
import thelm.jaopca.compat.energizedpower.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class EnergizedPowerHelper {

	public static final EnergizedPowerHelper INSTANCE = new EnergizedPowerHelper();

	private EnergizedPowerHelper() {}

	public boolean registerPulverizerRecipe(Identifier key, Object input, Object output, double[] chances, double[] chancesAdvanced, Object secondOutput, double[] secondChances, double[] secondChancesAdvanced) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, chances, chancesAdvanced, secondOutput, secondChances, secondChancesAdvanced));
	}

	public boolean registerPulverizerRecipe(Identifier key, Object input, Object output, double[] chances, double[] chancesAdvanced) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, chances, chancesAdvanced));
	}

	public boolean registerCompressorRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeSerializer(key, input, inputCount, output, outputCount));
	}

	public boolean registerAlloyFurnaceRecipe(Identifier key, Object[] input, Object output, int outputCount, Object secondOutput, double[] secondChances, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyFurnaceRecipeSerializer(key, input, output, outputCount, secondOutput, secondChances, time));
	}

	public boolean registerAlloyFurnaceRecipe(Identifier key, Object[] input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyFurnaceRecipeSerializer(key, input, output, outputCount, time));
	}
}
