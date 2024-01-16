package thelm.jaopca.compat.electrodynamics;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.electrodynamics.recipes.ChemicalCrystallizerRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.LatheRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.MineralCrusherRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.MineralGrinderRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.MineralWasherRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class ElectrodynamicsHelper {

	public static final ElectrodynamicsHelper INSTANCE = new ElectrodynamicsHelper();

	private ElectrodynamicsHelper() {}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSerializer(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSerializer(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSerializer(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerChemicalCrystallizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalCrystallizerRecipeSerializer(key, input, inputAmount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralWasherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralWasherRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, experience, time, energy));
	}
}
