package thelm.jaopca.compat.magneticraft;

import thelm.jaopca.compat.magneticraft.recipes.CrusherRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.GrinderRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.HammerTableRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.SifterRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class MagneticraftHelper {

	public static final MagneticraftHelper INSTANCE = new MagneticraftHelper();

	private MagneticraftHelper() {}

	public boolean registerHammerTableRecipe(String key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new HammerTableRecipeAction(key, input, output, outputCount));
	}

	public boolean registerCrusherRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance));
	}

	public boolean registerCrusherRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance));
	}

	public boolean registerCrusherRecipe(String key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, output, outputCount));
	}

	public boolean registerGrinderRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance));
	}

	public boolean registerGrinderRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance));
	}

	public boolean registerGrinderRecipe(String key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount));
	}

	public boolean registerSifterRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SifterRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance));
	}

	public boolean registerSifterRecipe(String key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SifterRecipeAction(key, input, output, outputCount));
	}
}
