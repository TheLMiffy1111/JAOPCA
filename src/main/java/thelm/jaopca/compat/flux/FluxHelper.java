package thelm.jaopca.compat.flux;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.flux.recipes.AlloyingRecipeSupplier;
import thelm.jaopca.compat.flux.recipes.CompactingRecipeSupplier;
import thelm.jaopca.compat.flux.recipes.GrindingRecipeSupplier;
import thelm.jaopca.compat.flux.recipes.WashingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class FluxHelper {

	public static final FluxHelper INSTANCE = new FluxHelper();

	private FluxHelper() {}

	public boolean registerGrindingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerGrindingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerGrindingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerGrindingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}
}
