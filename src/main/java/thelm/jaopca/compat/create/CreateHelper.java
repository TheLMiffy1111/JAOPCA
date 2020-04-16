package thelm.jaopca.compat.create;

import com.simibubi.create.modules.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.modules.contraptions.processing.ProcessingOutput;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.recipes.CrushingRecipeSupplier;
import thelm.jaopca.compat.create.recipes.MillingRecipeSupplier;
import thelm.jaopca.compat.create.recipes.PressingRecipeSupplier;
import thelm.jaopca.compat.create.recipes.SplashingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CreateHelper {

	public static final CreateHelper INSTANCE = new CreateHelper();

	private CreateHelper() {}

	public ProcessingOutput getProcessingOutput(Object obj, int count) {
		return new ProcessingOutput(MiscHelper.INSTANCE.getItemStack(obj, count), 1);
	}

	public ProcessingOutput getProcessingOutput(Object obj, int count, float chance) {
		return new ProcessingOutput(MiscHelper.INSTANCE.getItemStack(obj, count), chance);
	}

	public ProcessingIngredient getProcessingIngredient(Object obj) {
		return new ProcessingIngredient(MiscHelper.INSTANCE.getIngredient(obj));
	}

	public ProcessingIngredient getProcessingIngredient(Object obj, float returnChance) {
		return new ProcessingIngredient(MiscHelper.INSTANCE.getIngredient(obj), returnChance);
	}

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
