package thelm.jaopca.compat.hydcraft;

import thelm.jaopca.compat.hydcraft.recipes.CrushingRecipeAction;
import thelm.jaopca.compat.hydcraft.recipes.WasherRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class HydCraftHelper {

	public static final HydCraftHelper INSTANCE = new HydCraftHelper();

	private HydCraftHelper() {}

	public boolean registerCrushingRecipe(String key, Object input, Object output, int count, float pressure, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeAction(key, input, output, count, pressure, time));
	}

	public boolean registerWasherRecipe(String key, Object input, Object output, int count, float pressure, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WasherRecipeAction(key, input, output, count, pressure, time));
	}
}
