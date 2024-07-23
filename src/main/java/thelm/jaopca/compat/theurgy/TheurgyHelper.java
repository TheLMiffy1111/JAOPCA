package thelm.jaopca.compat.theurgy;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.theurgy.recipes.IncubationRecipeSerializer;
import thelm.jaopca.compat.theurgy.recipes.LiquefactionRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class TheurgyHelper {

	public static final TheurgyHelper INSTANCE = new TheurgyHelper();

	private TheurgyHelper() {}

	public boolean registerLiquefactionRecipe(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LiquefactionRecipeSerializer(key, itemInput, fluidInput, fluidInputAmount, output, outputCount, time));
	}

	public boolean registerIncubationRecipe(ResourceLocation key, Object mercury, Object salt, Object sulfur, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IncubationRecipeSerializer(key, mercury, salt, sulfur, output, outputCount, time));
	}
}
