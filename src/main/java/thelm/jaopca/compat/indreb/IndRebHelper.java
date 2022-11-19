package thelm.jaopca.compat.indreb;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.indreb.recipes.CompressingRecipeSerializer;
import thelm.jaopca.compat.indreb.recipes.CrushingRecipeSerializer;
import thelm.jaopca.compat.indreb.recipes.OreWashingRecipeSerializer;
import thelm.jaopca.compat.indreb.recipes.RollingRecipeSerializer;
import thelm.jaopca.compat.indreb.recipes.ThermalCentrifugingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class IndRebHelper {

	public static final IndRebHelper INSTANCE = new IndRebHelper();

	private IndRebHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, count, secondOutput, secondOutputCount, secondChance, time, power, experience));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, time, power, experience));
	}

	public boolean registerOreWashingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreWashingRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputCount, secondOutput, secondOutputCount, time, power, experience));
	}

	public boolean registerOreWashingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreWashingRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputCount, time, power, experience));
	}

	public boolean registerThermalCentrifugingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, int temperature, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ThermalCentrifugingRecipeSerializer(key, input, inputCount, output, count, secondOutput, secondOutputCount, temperature, time, power, experience));
	}

	public boolean registerThermalCentrifugingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int temperature, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ThermalCentrifugingRecipeSerializer(key, input, inputCount, output, outputCount, temperature, time, power, experience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int count, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, count, secondOutput, secondOutputCount, secondChance, time, power, experience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, outputCount, time, power, experience));
	}

	public boolean registerRollingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RollingRecipeSerializer(key, input, inputCount, output, outputCount, time, power, experience));
	}
}
