package thelm.jaopca.compat.techreborn;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.techreborn.recipes.CompressorRecipeSerializer;
import thelm.jaopca.compat.techreborn.recipes.GrinderRecipeSerializer;
import thelm.jaopca.compat.techreborn.recipes.ImplosionCompressorRecipeSerializer;
import thelm.jaopca.compat.techreborn.recipes.IndustrialGrinderRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class TechRebornHelper {

	public static final TechRebornHelper INSTANCE = new TechRebornHelper();

	private TechRebornHelper() {}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, inputCount, output, outputCount, power, time));
	}

	public boolean registerIndustrialGrinderRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, long fluidInputAmount, int power, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IndustrialGrinderRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, power, time, output));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeSerializer(key, input, inputCount, output, outputCount, power, time));
	}

	public boolean registerImplosionCompressorRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressorRecipeSerializer(key, input, inputCount, secondInput, secondInputCount, output, outputCount, secondOutput, secondOutputCount, power, time));
	}
}
