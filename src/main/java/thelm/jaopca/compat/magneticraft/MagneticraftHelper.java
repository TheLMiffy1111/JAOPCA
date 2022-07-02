package thelm.jaopca.compat.magneticraft;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.magneticraft.recipes.CrushingTableRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.GrinderRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.HydraulicPressRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.SieveRecipeAction;
import thelm.jaopca.compat.magneticraft.recipes.SluiceBoxRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class MagneticraftHelper {

	public static final MagneticraftHelper INSTANCE = new MagneticraftHelper();

	private MagneticraftHelper() {}

	public boolean registerCrushingTableRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingTableRecipeAction(key, input, output, count));
	}

	public boolean registerSluiceBoxRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SluiceBoxRecipeAction(key, input, output));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, float time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, float time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, time));
	}

	public boolean registerSieveRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance, float time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SieveRecipeAction(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance, time));
	}

	public boolean registerSieveRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, float time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SieveRecipeAction(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondOutputChance, time));
	}

	public boolean registerSieveRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, float time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SieveRecipeAction(key, input, output, outputCount, outputChance, time));
	}

	public boolean registerHydraulicPressRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float time, int mode) {
		return ApiImpl.INSTANCE.registerRecipe(key, new HydraulicPressRecipeAction(key, input, inputCount, output, outputCount, time, mode));
	}
}
