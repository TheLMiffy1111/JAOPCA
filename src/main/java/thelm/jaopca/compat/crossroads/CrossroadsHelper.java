package thelm.jaopca.compat.crossroads;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.crossroads.recipes.BlastFurnaceRecipeSupplier;
import thelm.jaopca.compat.crossroads.recipes.CrucibleRecipeSupplier;
import thelm.jaopca.compat.crossroads.recipes.FluidCoolingRecipeSupplier;
import thelm.jaopca.compat.crossroads.recipes.MillRecipeSupplier;
import thelm.jaopca.compat.crossroads.recipes.OreCleanserRecipeSupplier;
import thelm.jaopca.compat.crossroads.recipes.StampMillRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class CrossroadsHelper {

	public static final CrossroadsHelper INSTANCE = new CrossroadsHelper();

	private CrossroadsHelper() {}

	public boolean registerBlastFurnaceRecipe(ResourceLocation key, String group, Object input, Object output, int amount, int slagCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlastFurnaceRecipeSupplier(key, group, input, output, amount, slagCount));
	}

	public boolean registerBlastFurnaceRecipe(ResourceLocation key, Object input, Object output, int amount, int slagCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlastFurnaceRecipeSupplier(key, input, output, amount, slagCount));
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, String group, Object input, Object output, int amount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSupplier(key, group, input, output, amount));
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, Object input, Object output, int amount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSupplier(key, input, output, amount));
	}

	public boolean registerFluidCoolingRecipe(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidCoolingRecipeSupplier(key, group, input, inputAmount, output, outputCount, maxTemp, addedHeat));
	}

	public boolean registerFluidCoolingRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidCoolingRecipeSupplier(key, input, inputAmount, output, outputCount, maxTemp, addedHeat));
	}

	public boolean registerMillRecipe(ResourceLocation key, String group, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillRecipeSupplier(key, group, input, output));
	}

	public boolean registerMillRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillRecipeSupplier(key, input, output));
	}

	public boolean registerOreCleanserRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreCleanserRecipeSupplier(key, group, input, output, count));
	}

	public boolean registerOreCleanserRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreCleanserRecipeSupplier(key, input, output, count));
	}

	public boolean registerStampMillRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampMillRecipeSupplier(key, group, input, output, count));
	}

	public boolean registerStampMillRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampMillRecipeSupplier(key, input, output, count));
	}
}
