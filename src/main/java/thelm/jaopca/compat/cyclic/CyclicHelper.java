package thelm.jaopca.compat.cyclic;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.cyclic.recipes.CrusherRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class CyclicHelper {

	public static final CyclicHelper INSTANCE = new CyclicHelper();

	private CyclicHelper() {}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, secondChance, time, power));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSerializer(key, input, output, outputCount, time, power));
	}
}
