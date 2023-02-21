package thelm.jaopca.compat.mariculture;

import thelm.jaopca.compat.mariculture.recipes.BlockCastingRecipeAction;
import thelm.jaopca.compat.mariculture.recipes.CrucibleRecipeAction;
import thelm.jaopca.compat.mariculture.recipes.IngotCastingRecipeAction;
import thelm.jaopca.compat.mariculture.recipes.NuggetCastingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class MaricultureHelper {

	public static final MaricultureHelper INSTANCE = new MaricultureHelper();

	private MaricultureHelper() {}

	public boolean registerCrucibleRecipe(String key, Object input, int inputCount, Object fluidOutput, int fluidOutputAmount, Object itemOutput, int itemOutputCount, int itemOutputChance, int temperature) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeAction(key, input, inputCount, fluidOutput, fluidOutputAmount, itemOutput, itemOutputCount, itemOutputChance, temperature));
	}

	public boolean registerCrucibleRecipe(String key, Object input, int inputCount, Object fluidOutput, int fluidOutputAmount, int temperature) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeAction(key, input, inputCount, fluidOutput, fluidOutputAmount, temperature));
	}

	public boolean registerIngotCastingRecipe(String key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotCastingRecipeAction(key, input, inputAmount, output, outputCount));
	}

	public boolean registerNuggetCastingRecipe(String key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new NuggetCastingRecipeAction(key, input, inputAmount, output, outputCount));
	}

	public boolean registerBlockCastingRecipe(String key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlockCastingRecipeAction(key, input, inputAmount, output, outputCount));
	}
}
