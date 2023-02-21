package thelm.jaopca.compat.factorization;

import thelm.jaopca.compat.factorization.recipes.CrystallizerRecipeAction;
import thelm.jaopca.compat.factorization.recipes.GrinderRecipeAction;
import thelm.jaopca.compat.factorization.recipes.SlagFurnaceRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class FactorizationHelper {

	public static final FactorizationHelper INSTANCE = new FactorizationHelper();

	private FactorizationHelper() {}

	public boolean registerGrinderRecipe(String key, Object input, Object output, float chance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, chance));
	}

	public boolean registerSlagFurnaceRecipe(String key, Object input, Object firstOutput, float firstOutputChance, Object secondOutput, float secondOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SlagFurnaceRecipeAction(key, input, firstOutput, firstOutputChance, secondOutput, secondOutputChance));
	}

	public boolean registerCrystallizerRecipe(String key, Object input, int inputCount, Object solution, int solutionCount, Object output, float outputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizerRecipeAction(key, input, inputCount, solution, solutionCount, output, outputChance));
	}
}
