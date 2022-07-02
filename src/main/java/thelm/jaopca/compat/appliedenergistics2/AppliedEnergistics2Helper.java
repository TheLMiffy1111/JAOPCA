package thelm.jaopca.compat.appliedenergistics2;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.appliedenergistics2.recipes.GrinderRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class AppliedEnergistics2Helper {

	public static final AppliedEnergistics2Helper INSTANCE = new AppliedEnergistics2Helper();

	private AppliedEnergistics2Helper() {}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance, int turns) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance, turns));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int turns) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, turns));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, int turns) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeAction(key, input, output, outputCount, turns));
	}
}
