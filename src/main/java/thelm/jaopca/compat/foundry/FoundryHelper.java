package thelm.jaopca.compat.foundry;

import java.util.function.ToIntFunction;

import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.foundry.recipes.AtomizerRecipeAction;
import thelm.jaopca.compat.foundry.recipes.CastingRecipeAction;
import thelm.jaopca.compat.foundry.recipes.MeltingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class FoundryHelper {

	public static final FoundryHelper INSTANCE = new FoundryHelper();

	private FoundryHelper() {}

	public boolean registerMeltingRecipe(String key, Object input, int inputCount, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> speed) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeAction(key, input, inputCount, output, outputAmount, temperature, speed));
	}

	public boolean registerCastingRecipe(String key, Object fluidInput, int fluidInputAmount, Object mold, Object itemInput, int itemInputCount, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingRecipeAction(key, fluidInput, fluidInputAmount, mold, itemInput, itemInputCount, output, outputCount, speed));
	}

	public boolean registerCastingRecipe(String key, Object fluidInput, int fluidInputAmount, Object mold, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingRecipeAction(key, fluidInput, fluidInputAmount, mold, output, outputCount, speed));
	}

	public boolean registerAtomizerRecipe(String key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AtomizerRecipeAction(key, input, inputAmount, output, outputCount));
	}
}
