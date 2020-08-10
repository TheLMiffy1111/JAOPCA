package thelm.jaopca.compat.appliedenergistics2;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.appliedenergistics2.recipes.GrinderRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class AppliedEnergistics2Helper {

	public static final AppliedEnergistics2Helper INSTANCE = new AppliedEnergistics2Helper();

	private AppliedEnergistics2Helper() {}

	public boolean registerGrinderRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int turns, Object... extraOutputs) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSupplier(key, group, input, inputCount, output, outputCount, turns, extraOutputs));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int turns, Object... extraOutputs) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSupplier(key, input, inputCount, output, outputCount, turns, extraOutputs));
	}
}
