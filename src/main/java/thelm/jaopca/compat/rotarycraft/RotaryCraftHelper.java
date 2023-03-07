package thelm.jaopca.compat.rotarycraft;

import thelm.jaopca.compat.rotarycraft.recipes.CrystallizerRecipeAction;
import thelm.jaopca.compat.rotarycraft.recipes.ExtractorRecipeAction;
import thelm.jaopca.compat.rotarycraft.recipes.GrinderRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class RotaryCraftHelper {

	public static final RotaryCraftHelper INSTANCE = new RotaryCraftHelper();

	private RotaryCraftHelper() {}

	public boolean registerGrinderRecipe(String key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, count));
	}

	public boolean registerExtractorRecipe(String key, Object ore, Object dust, Object slurry, Object solution, Object flakes, boolean isRare) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ExtractorRecipeAction(key, ore, dust, slurry, solution, flakes, isRare));
	}

	public boolean registerCrystallizerRecipe(String key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizerRecipeAction(key, input, inputAmount, output, outputCount));
	}
}
