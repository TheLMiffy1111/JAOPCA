package thelm.jaopca.compat.factorium;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.factorium.recipes.CrusherRecipeSerializer;
import thelm.jaopca.compat.factorium.recipes.ExtruderRecipeSerializer;
import thelm.jaopca.compat.factorium.recipes.GrinderRecipeSerializer;
import thelm.jaopca.compat.factorium.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class FactoriumHelper {

	public static final FactoriumHelper INSTANCE = new FactoriumHelper();

	private FactoriumHelper() {}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSerializer(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSerializer(key, input, output, outputCount, outputChance));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, outputChance));
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondOutputChance, thirdOutput, thirdOutputCount, thirdOutputChance));
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, outputChance));
	}

	public boolean registerExtruderRecipe(ResourceLocation key, Object input, int inputCount, Object die, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ExtruderRecipeSerializer(key, input, inputCount, die, output, outputCount));
	}
}
