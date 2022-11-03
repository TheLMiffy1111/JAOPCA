package thelm.jaopca.compat.indreb;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.indreb.recipes.CompressingRecipeSerializer;
import thelm.jaopca.compat.indreb.recipes.CrushingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class IndRebHelper {

	public static final IndRebHelper INSTANCE = new IndRebHelper();

	private IndRebHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, group, input, inputCount, output, count, secondOutput, secondOutputCount, secondChance, time, power, experience));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, count, secondOutput, secondOutputCount, secondChance, time, power, experience));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, group, input, inputCount, output, outputCount, time, power, experience));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, time, power, experience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, group, input, inputCount, output, count, secondOutput, secondOutputCount, secondChance, time, power, experience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, count, secondOutput, secondOutputCount, secondChance, time, power, experience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, group, input, inputCount, output, outputCount, time, power, experience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, outputCount, time, power, experience));
	}
}
