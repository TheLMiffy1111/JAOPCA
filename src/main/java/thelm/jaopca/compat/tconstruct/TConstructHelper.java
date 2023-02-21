package thelm.jaopca.compat.tconstruct;

import thelm.jaopca.compat.tconstruct.recipes.BasinCastingRecipeAction;
import thelm.jaopca.compat.tconstruct.recipes.MeltingRecipeAction;
import thelm.jaopca.compat.tconstruct.recipes.TableCastingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class TConstructHelper {

	public static final TConstructHelper INSTANCE = new TConstructHelper();

	private TConstructHelper() {}

	public boolean registerMeltingRecipe(String key, Object input, Object render, Object output, int outputAmount, int temperature) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeAction(key, input, render, output, outputAmount, temperature));
	}

	public boolean registerTableCastingRecipe(String key, Object cast, Object input, int inputAmount, Object output, int time, boolean consumeCast) {
		return ApiImpl.INSTANCE.registerRecipe(key, new TableCastingRecipeAction(key, cast, input, inputAmount, output, time, consumeCast));
	}

	public boolean registerBasinCastingRecipe(String key, Object cast, Object input, int inputAmount, Object output, int time, boolean consumeCast) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BasinCastingRecipeAction(key, cast, input, inputAmount, output, time, consumeCast));
	}
}
