package thelm.jaopca.compat.oritech;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.oritech.recipes.AtomicForgeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.CentrifugeFluidRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.CentrifugeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.FoundryRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.GrinderRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class OritechHelper {

	public static final OritechHelper INSTANCE = new OritechHelper();

	private OritechHelper() {}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object thirdOutput, int thirdOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, thirdOutput, thirdOutputCount, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCentrifugeFluidRecipe(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeFluidRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, secondOutput, secondOutputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerCentrifugeFluidRecipe(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeFluidRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerFoundryRecipe(ResourceLocation key, Object input, Object secondInput, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FoundryRecipeSerializer(key, input, secondInput, output, outputCount, time));
	}

	public boolean registerAtomicForgeRecipe(ResourceLocation key, Object input, Object secondInput, Object thirdInput, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AtomicForgeRecipeSerializer(key, input, secondInput, thirdInput, output, outputCount, time));
	}
}
