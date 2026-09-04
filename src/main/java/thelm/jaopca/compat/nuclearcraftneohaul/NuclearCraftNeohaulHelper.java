package thelm.jaopca.compat.nuclearcraftneohaul;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.nuclearcraftneohaul.recipes.IngotFormerRecipeSerializer;
import thelm.jaopca.compat.nuclearcraftneohaul.recipes.ManufactoryRecipeSerializer;
import thelm.jaopca.compat.nuclearcraftneohaul.recipes.MelterRecipeSerializer;
import thelm.jaopca.compat.nuclearcraftneohaul.recipes.PressurizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class NuclearCraftNeohaulHelper {

	public static final NuclearCraftNeohaulHelper INSTANCE = new NuclearCraftNeohaulHelper();

	private NuclearCraftNeohaulHelper() {}

	public boolean registerManufactoryRecipe(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ManufactoryRecipeSerializer(key, input, inputCount, inputChance, inputMin, output, outputCount, outputChance, outputMin, radiation, time, power));
	}

	public boolean registerManufactoryRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ManufactoryRecipeSerializer(key, input, inputCount, output, outputCount, outputChance, outputMin, radiation, time, power));
	}

	public boolean registerManufactoryRecipe(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ManufactoryRecipeSerializer(key, input, inputCount, inputChance, inputMin, output, outputCount, radiation, time, power));
	}

	public boolean registerManufactoryRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ManufactoryRecipeSerializer(key, input, inputCount, output, outputCount, radiation, time, power));
	}

	public boolean registerIngotFormerRecipe(ResourceLocation key, Object input, int inputAmount, int inputChance, int inputIncrement, int inputMin, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotFormerRecipeSerializer(key, input, inputAmount, inputChance, inputIncrement, inputMin, output, outputCount, outputChance, outputMin, radiation, time, power));
	}

	public boolean registerIngotFormerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotFormerRecipeSerializer(key, input, inputAmount, output, outputCount, outputChance, outputMin, radiation, time, power));
	}

	public boolean registerIngotFormerRecipe(ResourceLocation key, Object input, int inputAmount, int inputChance, int inputIncrement, int inputMin, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotFormerRecipeSerializer(key, input, inputAmount, inputChance, inputIncrement, inputMin, output, outputCount, radiation, time, power));
	}

	public boolean registerIngotFormerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotFormerRecipeSerializer(key, input, inputAmount, output, outputCount, radiation, time, power));
	}

	public boolean registerMelterRecipe(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputAmount, int outputChance, int outputIncrement, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MelterRecipeSerializer(key, input, inputCount, inputChance, inputMin, output, outputAmount, outputChance, outputIncrement, outputMin, radiation, time, power));
	}

	public boolean registerMelterRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, int outputChance, int outputIncrement, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MelterRecipeSerializer(key, input, inputCount, output, outputAmount, outputChance, outputIncrement, outputMin, radiation, time, power));
	}

	public boolean registerMelterRecipe(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputAmount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MelterRecipeSerializer(key, input, inputCount, inputChance, inputMin, output, outputAmount, radiation, time, power));
	}

	public boolean registerMelterRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MelterRecipeSerializer(key, input, inputCount, output, outputAmount, radiation, time, power));
	}

	public boolean registerPressurizerRecipe(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressurizerRecipeSerializer(key, input, inputCount, inputChance, inputMin, output, outputCount, outputChance, outputMin, radiation, time, power));
	}

	public boolean registerPressurizerRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressurizerRecipeSerializer(key, input, inputCount, output, outputCount, outputChance, outputMin, radiation, time, power));
	}

	public boolean registerPressurizerRecipe(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressurizerRecipeSerializer(key, input, inputCount, inputChance, inputMin, output, outputCount, radiation, time, power));
	}

	public boolean registerPressurizerRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressurizerRecipeSerializer(key, input, inputCount, output, outputCount, radiation, time, power));
	}
}
