package thelm.jaopca.compat.teslathingies;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.teslathingies.recipes.CompoundMakerRecipeAction;
import thelm.jaopca.compat.teslathingies.recipes.PowderMakerRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class TeslaThingiesHelper {

	public static final TeslaThingiesHelper INSTANCE = new TeslaThingiesHelper();

	private TeslaThingiesHelper() {}

	public boolean registerCompoundMakerRecipe(ResourceLocation key, Object[] top, Object left, int leftAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompoundMakerRecipeAction(key, top, left, leftAmount, output, outputCount));
	}

	public boolean registerCompoundMakerRecipe(ResourceLocation key, Object[] top, Object[] bottom, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompoundMakerRecipeAction(key, top, bottom, output, outputCount));
	}

	public boolean registerCompoundMakerRecipe(ResourceLocation key, Object[] top, Object left, int leftAmount, Object[] bottom, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompoundMakerRecipeAction(key, top, left, leftAmount, bottom, output, outputCount));
	}

	public boolean registerCompoundMakerRecipe(ResourceLocation key, Object[] top, Object left, int leftAmount, Object[] bottom, Object right, int rightAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompoundMakerRecipeAction(key, top, left, leftAmount, bottom, right, rightAmount, output, outputCount));
	}

	public boolean registerPowderMakerRecipe(ResourceLocation key, Object input, int inputCount, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PowderMakerRecipeAction(key, input, inputCount, output));
	}
}
