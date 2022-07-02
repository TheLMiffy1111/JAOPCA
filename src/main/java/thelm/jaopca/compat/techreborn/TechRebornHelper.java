package thelm.jaopca.compat.techreborn;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.techreborn.recipes.CompressorRecipeAction;
import thelm.jaopca.compat.techreborn.recipes.GrinderRecipeAction;
import thelm.jaopca.compat.techreborn.recipes.ImplosionCompressorRecipeAction;
import thelm.jaopca.compat.techreborn.recipes.IndustrialGrinderRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class TechRebornHelper {

	public static final TechRebornHelper INSTANCE = new TechRebornHelper();

	private TechRebornHelper() {}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, inputCount, output, outputCount, time, energy));
	}

	public boolean registerIndustrialGrinderRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, int time, int energy, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IndustrialGrinderRecipeAction(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, time, energy, output));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeAction(key, input, inputCount, output, outputCount, time, energy));
	}

	public boolean registerImplosionCompressorRecipe(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressorRecipeAction(key, input, output, time, energy));
	}
}
