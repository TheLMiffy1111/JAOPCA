package thelm.jaopca.compat.create;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.create.recipes.CrushingRecipeSerializer;
import thelm.jaopca.compat.create.recipes.MillingRecipeSerializer;
import thelm.jaopca.compat.create.recipes.PressingRecipeSerializer;
import thelm.jaopca.compat.create.recipes.SplashingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class CreateHelper {

	public static final CreateHelper INSTANCE = new CreateHelper();
	private static final Logger LOGGER = LogManager.getLogger();

	private CreateHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, time, output));
	}

	public boolean registerMillingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillingRecipeSerializer(key, input, time, output));
	}

	public boolean registerPressingRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressingRecipeSerializer(key, input, output, outputCount));
	}

	public boolean registerSplashingRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SplashingRecipeSerializer(key, input, output));
	}
}
