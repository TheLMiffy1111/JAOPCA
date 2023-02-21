package thelm.jaopca.compat.railcraft;

import thelm.jaopca.compat.railcraft.recipes.RockCrusherRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class RailcraftHelper {

	public static final RailcraftHelper INSTANCE = new RailcraftHelper();

	private RailcraftHelper() {}

	public boolean registerRockCrusherRecipe(String key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RockCrusherRecipeAction(key, input, time, output));
	}
}
