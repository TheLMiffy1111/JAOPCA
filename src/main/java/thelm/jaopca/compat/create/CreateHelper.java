package thelm.jaopca.compat.create;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.recipes.CompactingRecipeAction;
import thelm.jaopca.compat.create.recipes.MillingRecipeAction;
import thelm.jaopca.compat.create.recipes.PressingRecipeAction;
import thelm.jaopca.compat.create.recipes.WashingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class CreateHelper {

	public static final CreateHelper INSTANCE = new CreateHelper();

	private CreateHelper() {}

	public boolean registerWashingRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount));
	}

	public boolean registerMillingRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondOutputChance, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillingRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time));
	}

	public boolean registerPressingRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressingRecipeAction(key, input, output, outputCount));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int heatLevel) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeAction(key, input, inputCount, output, outputCount, heatLevel));
	}
}
