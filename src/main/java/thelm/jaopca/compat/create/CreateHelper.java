package thelm.jaopca.compat.create;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.recipes.CrushingRecipeSupplier;
import thelm.jaopca.compat.create.recipes.MillingRecipeSupplier;
import thelm.jaopca.compat.create.recipes.PressingRecipeSupplier;
import thelm.jaopca.compat.create.recipes.SplashingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class CreateHelper {

	public static final CreateHelper INSTANCE = new CreateHelper();

	private CreateHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, time, output));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, group, input, time, output));
	}

	public boolean registerMillingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillingRecipeSupplier(key, input, time, output));
	}

	public boolean registerMillingRecipe(ResourceLocation key, String group, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillingRecipeSupplier(key, group, input, time, output));
	}

	public boolean registerPressingRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressingRecipeSupplier(key, input, output, outputCount));
	}

	public boolean registerPressingRecipe(ResourceLocation key, String group, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressingRecipeSupplier(key, group, input, output, outputCount));
	}

	public boolean registerSplashingRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SplashingRecipeSupplier(key, input, output));
	}

	public boolean registerSplashingRecipe(ResourceLocation key, String group, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SplashingRecipeSupplier(key, group, input, output));
	}
}
