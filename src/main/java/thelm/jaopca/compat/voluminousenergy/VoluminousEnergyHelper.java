package thelm.jaopca.compat.voluminousenergy;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.voluminousenergy.recipes.CompressingRecipeSupplier;
import thelm.jaopca.compat.voluminousenergy.recipes.CrushingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class VoluminousEnergyHelper {

	public static final VoluminousEnergyHelper INSTANCE = new VoluminousEnergyHelper();

	private VoluminousEnergyHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, inputCount, output, outputCount, time));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSupplier(key, input, inputCount, output, outputCount, time));
	}
}
