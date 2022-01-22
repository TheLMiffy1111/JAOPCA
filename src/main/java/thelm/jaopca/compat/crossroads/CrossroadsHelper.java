package thelm.jaopca.compat.crossroads;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.crossroads.recipes.BlastFurnaceRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.CrucibleRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.FluidCoolingRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.MillRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.OreCleanserRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.StampMillRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class CrossroadsHelper {

	public static final CrossroadsHelper INSTANCE = new CrossroadsHelper();

	private CrossroadsHelper() {}

	public boolean registerBlastFurnaceRecipe(ResourceLocation key, String group, Object input, Object output, int amount, int slagCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlastFurnaceRecipeSerializer(key, group, input, output, amount, slagCount));
	}

	public boolean registerBlastFurnaceRecipe(ResourceLocation key, Object input, Object output, int amount, int slagCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlastFurnaceRecipeSerializer(key, input, output, amount, slagCount));
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, String group, Object input, Object output, int amount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSerializer(key, group, input, output, amount));
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, Object input, Object output, int amount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSerializer(key, input, output, amount));
	}

	public boolean registerFluidCoolingRecipe(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidCoolingRecipeSerializer(key, group, input, inputAmount, output, outputCount, maxTemp, addedHeat));
	}

	public boolean registerFluidCoolingRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidCoolingRecipeSerializer(key, input, inputAmount, output, outputCount, maxTemp, addedHeat));
	}

	public boolean registerMillRecipe(ResourceLocation key, String group, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillRecipeSerializer(key, group, input, output));
	}

	public boolean registerMillRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillRecipeSerializer(key, input, output));
	}

	public boolean registerOreCleanserRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreCleanserRecipeSerializer(key, group, input, output, count));
	}

	public boolean registerOreCleanserRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreCleanserRecipeSerializer(key, input, output, count));
	}

	public boolean registerStampMillRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampMillRecipeSerializer(key, group, input, output, count));
	}

	public boolean registerStampMillRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampMillRecipeSerializer(key, input, output, count));
	}
}
