package thelm.jaopca.compat.silentsmechanisms;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.silentsmechanisms.recipes.CompressingRecipeSupplier;
import thelm.jaopca.compat.silentsmechanisms.recipes.CrushingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class SilentsMechanismsHelper {

	public static final SilentsMechanismsHelper INSTANCE = new SilentsMechanismsHelper();

	private SilentsMechanismsHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, time, output));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSupplier(key, input, inputCount, output, outputCount, time));
	}
}
