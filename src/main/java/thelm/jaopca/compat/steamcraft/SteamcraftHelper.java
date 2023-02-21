package thelm.jaopca.compat.steamcraft;

import thelm.jaopca.compat.steamcraft.recipes.SmasherRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class SteamcraftHelper {

	public static final SteamcraftHelper INSTANCE = new SteamcraftHelper();

	private SteamcraftHelper() {}

	public boolean registerSmasherRecipe(String key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmasherRecipeAction(key, input, output, count));
	}
}
