package thelm.jaopca.compat.create;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.recipes.CrushingRecipeAction;
import thelm.jaopca.compat.create.recipes.MillingRecipeAction;
import thelm.jaopca.compat.create.recipes.PressingRecipeAction;
import thelm.jaopca.compat.create.recipes.WashingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class CreateHelper {

	public static final CreateHelper INSTANCE = new CreateHelper();

	private CreateHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeAction(key, input, time, output));
	}

	public boolean registerMillingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillingRecipeAction(key, input, time, output));
	}

	public boolean registerPressingRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressingRecipeAction(key, input, output, outputCount));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeAction(key, input, output));
	}
}
