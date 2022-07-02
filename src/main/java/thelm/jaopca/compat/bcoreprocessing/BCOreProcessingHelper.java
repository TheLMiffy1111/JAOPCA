package thelm.jaopca.compat.bcoreprocessing;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.bcoreprocessing.recipes.CoolableRecipeAction;
import thelm.jaopca.compat.bcoreprocessing.recipes.FluidProcessorRecipeAction;
import thelm.jaopca.compat.bcoreprocessing.recipes.HeatableRecipeAction;
import thelm.jaopca.compat.bcoreprocessing.recipes.OreProcessorRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class BCOreProcessingHelper {

	public static final BCOreProcessingHelper INSTANCE = new BCOreProcessingHelper();

	private BCOreProcessingHelper() {}

	public boolean registerHeatableRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, int heatFrom, int heatTo) {
		return ApiImpl.INSTANCE.registerRecipe(key, new HeatableRecipeAction(key, input, inputAmount, output, outputAmount, heatFrom, heatTo));
	}

	public boolean registerCoolableRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, int heatFrom, int heatTo) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CoolableRecipeAction(key, input, inputAmount, output, outputAmount, heatFrom, heatTo));
	}

	public boolean registerOreProcessorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, Object secondOutput, int secondOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreProcessorRecipeAction(key, input, inputCount, output, outputAmount, secondOutput, secondOutputAmount, time));
	}

	public boolean registerFluidProcessorRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidProcessorRecipeAction(key, input, inputAmount, output, outputCount, fluidOutput, fluidOutputAmount, time));
	}
}
