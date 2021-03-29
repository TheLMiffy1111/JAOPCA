package thelm.jaopca.compat.advancedrocketry;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.advancedrocketry.recipes.LatheRecipeSupplier;
import thelm.jaopca.compat.advancedrocketry.recipes.RollingMachineRecipeSupplier;
import thelm.jaopca.compat.advancedrocketry.recipes.SmallPlateRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class AdvancedRocketryHelper {

	public static final AdvancedRocketryHelper INSTANCE = new AdvancedRocketryHelper();

	private AdvancedRocketryHelper() {}

	public boolean registerSmallPlateRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmallPlateRecipeSupplier(key, input, output, outputCount));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSupplier(key, input, output, time, energy));
	}

	public boolean registerRollingMachineRecipe(ResourceLocation key, Object[] itemInput, Object fluidInput, int fluidInputAmount, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RollingMachineRecipeSupplier(key, itemInput, fluidInput, fluidInputAmount, output, time, energy));
	}
}
