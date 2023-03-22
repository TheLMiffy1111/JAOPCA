package thelm.jaopca.compat.techreborn;

import thelm.jaopca.compat.techreborn.recipes.GrinderRecipeAction;
import thelm.jaopca.compat.techreborn.recipes.ImplosionCompressorRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class TechRebornHelper {

	public static final TechRebornHelper INSTANCE = new TechRebornHelper();

	private TechRebornHelper() {}

	public boolean registerGrinderRecipe(String key, Object[] input, Object fluidInput, int fluidInputAmount, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, fluidInput, fluidInputAmount, output, time, energy));
	}

	public boolean registerImplosionCompressorRecipe(String key, Object[] input, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressorRecipeAction(key, input, output, time, energy));
	}
}
