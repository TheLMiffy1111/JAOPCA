package thelm.jaopca.compat.wtbwmachines;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.wtbwmachines.recipes.CompressingRecipeSupplier;
import thelm.jaopca.compat.wtbwmachines.recipes.CrushingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class WTBWMachinesHelper {

	public static final WTBWMachinesHelper INSTANCE = new WTBWMachinesHelper();

	private WTBWMachinesHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, int time, int energy, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, inputCount, time, energy, output));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSupplier(key, input, inputCount, output, outputCount, time, energy));
	}
}
