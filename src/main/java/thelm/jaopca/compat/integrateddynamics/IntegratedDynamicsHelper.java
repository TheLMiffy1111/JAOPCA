package thelm.jaopca.compat.integrateddynamics;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.integrateddynamics.recipes.MechanicalSqueezerRecipeSupplier;
import thelm.jaopca.compat.integrateddynamics.recipes.SqueezerRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class IntegratedDynamicsHelper {

	public static final IntegratedDynamicsHelper INSTANCE = new IntegratedDynamicsHelper();

	private IntegratedDynamicsHelper() {}

	public boolean registerSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SqueezerRecipeSupplier(key, input, itemOutput, fluidOutput, fluidOutputAmount));
	}

	public boolean registerSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SqueezerRecipeSupplier(key, input, itemOutput));
	}

	public boolean registerMechanicalSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MechanicalSqueezerRecipeSupplier(key, input, itemOutput, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerMechanicalSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MechanicalSqueezerRecipeSupplier(key, input, itemOutput, time));
	}
}
