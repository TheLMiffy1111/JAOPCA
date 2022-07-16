package thelm.jaopca.compat.voluminousenergy;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.voluminousenergy.recipes.CompressingRecipeSerializer;
import thelm.jaopca.compat.voluminousenergy.recipes.CrushingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class VoluminousEnergyHelper {

	public static final VoluminousEnergyHelper INSTANCE = new VoluminousEnergyHelper();

	private VoluminousEnergyHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}
}
