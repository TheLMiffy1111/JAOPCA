package thelm.jaopca.compat.actuallyadditions;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.actuallyadditions.recipes.CrushingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class ActuallyAdditionsHelper {

	public static final ActuallyAdditionsHelper INSTANCE = new ActuallyAdditionsHelper();

	private ActuallyAdditionsHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondChance));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int outputCount, float outputChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, output, outputCount, outputChance));
	}
}
