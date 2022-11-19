package thelm.jaopca.compat.ic2;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.ic2.recipes.ExtractorRecipeSerializer;
import thelm.jaopca.compat.ic2.recipes.MaceratorRecipeSerializer;
import thelm.jaopca.compat.ic2.recipes.RefineryRecipeSerializer;

public class IC2Helper {

	public static final IC2Helper INSTANCE = new IC2Helper();

	private IC2Helper() {}

	public boolean registerMaceratorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time, double energy, float experience) {
		return IC2DataInjector.registerRecipe("macerator", key, new MaceratorRecipeSerializer(key, input, inputCount, output, outputCount, time, energy, experience));
	}

	public boolean registerExtractorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time, double energy, float experience) {
		return IC2DataInjector.registerRecipe("extractor", key, new ExtractorRecipeSerializer(key, input, inputCount, output, outputCount, time, energy, experience));
	}

	public boolean registerRefineryRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object secondFluidInput, int secondFluidInputAmount, Object itemOutput, int itemOutputCountMin, int itemOutputCountMax, Object fluidOutput, int fluidOutputAmountMin, int fluidOutputAmountMax, double time, double energy) {
		return IC2DataInjector.registerRecipe("refinery", key, new RefineryRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, secondFluidInput, secondFluidInputAmount, itemOutput, itemOutputCountMin, itemOutputCountMax, fluidOutput, fluidOutputAmountMin, fluidOutputAmountMax, time, energy));
	}

	public boolean registerRefineryRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object itemOutput, int itemOutputCountMin, int itemOutputCountMax, double time, double energy) {
		return IC2DataInjector.registerRecipe("refinery", key, new RefineryRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, itemOutput, itemOutputCountMin, itemOutputCountMax, time, energy));
	}
}
