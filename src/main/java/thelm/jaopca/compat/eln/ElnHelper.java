package thelm.jaopca.compat.eln;

import thelm.jaopca.compat.eln.recipes.MaceratorRecipeAction;
import thelm.jaopca.compat.eln.recipes.PlateMachineRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class ElnHelper {

	public static final ElnHelper INSTANCE = new ElnHelper();

	private ElnHelper() {}

	public boolean registerMaceratorRecipe(String key, Object input, int inputCount, Object output, int outputCount, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaceratorRecipeAction(key, input, inputCount, output, outputCount, power));
	}

	public boolean registerPlateMachineRecipe(String key, Object input, int inputCount, Object output, int outputCount, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PlateMachineRecipeAction(key, input, inputCount, output, outputCount, power));
	}
}
