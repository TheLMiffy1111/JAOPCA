package thelm.jaopca.compat.bcadditions;

import thelm.jaopca.compat.bcadditions.recipes.DusterRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class BCAdditionsHelper {

	public static final BCAdditionsHelper INSTANCE = new BCAdditionsHelper();

	private BCAdditionsHelper() {}

	public boolean registerDusterRecipe(String key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new DusterRecipeAction(key, input, output, count));
	}
}
