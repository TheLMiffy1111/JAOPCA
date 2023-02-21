package thelm.jaopca.compat.engtoolbox;

import thelm.jaopca.compat.engtoolbox.recipes.CentrifugeRecipeAction;
import thelm.jaopca.compat.engtoolbox.recipes.GrinderRecipeAction;
import thelm.jaopca.compat.engtoolbox.recipes.MultiSmelterRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class EngToolboxHelper {

	public static final EngToolboxHelper INSTANCE = new EngToolboxHelper();

	private EngToolboxHelper() {}

	public boolean registerGrinderRecipe(String key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, count));
	}

	public boolean registerMultiSmelterRecipe(String key, Object firstInput, Object secondInput, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MultiSmelterRecipeAction(key, firstInput, secondInput, output, count));
	}

	public boolean registerCentrifugeRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance));
	}
}
