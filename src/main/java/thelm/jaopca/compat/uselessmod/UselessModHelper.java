package thelm.jaopca.compat.uselessmod;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.compat.uselessmod.recipes.CompressingRecipeSupplier;
import thelm.jaopca.compat.uselessmod.recipes.CrushingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class UselessModHelper {

	public static final UselessModHelper INSTANCE = new UselessModHelper();

	private UselessModHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, Object output, int count, Object secondOutput, int secondCount, float secondChance, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, group, input, output, count, secondOutput, secondCount, secondChance, experience, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int count, Object secondOutput, int secondCount, float secondChance, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, output, count, secondOutput, secondCount, secondChance, experience, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, output, count, experience, time));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSupplier(key, input, output, count, experience, time));
	}
}
