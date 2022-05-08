package thelm.jaopca.compat.integrateddynamics;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.integrateddynamics.recipes.MechanicalSqueezerRecipeSerializer;
import thelm.jaopca.compat.integrateddynamics.recipes.SqueezerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class IntegratedDynamicsHelper {

	public static final IntegratedDynamicsHelper INSTANCE = new IntegratedDynamicsHelper();

	private IntegratedDynamicsHelper() {}

	public boolean registerSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SqueezerRecipeSerializer(key, input, itemOutput, fluidOutput, fluidOutputAmount));
	}

	public boolean registerSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SqueezerRecipeSerializer(key, input, itemOutput));
	}

	public boolean registerMechanicalSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MechanicalSqueezerRecipeSerializer(key, input, itemOutput, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerMechanicalSqueezerRecipe(ResourceLocation key, Object input, Object[] itemOutput, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MechanicalSqueezerRecipeSerializer(key, input, itemOutput, time));
	}
}
