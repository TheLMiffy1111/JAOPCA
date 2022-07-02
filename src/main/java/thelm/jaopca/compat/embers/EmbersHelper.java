package thelm.jaopca.compat.embers;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.embers.recipes.MeltingRecipeAction;
import thelm.jaopca.compat.embers.recipes.StampingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class EmbersHelper {

	public static final EmbersHelper INSTANCE = new EmbersHelper();

	private EmbersHelper() {}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount, Object secondOutput, int secondOutputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeAction(key, input, output, outputAmount, secondOutput, secondOutputAmount));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeAction(key, input, output, outputAmount));
	}

	public boolean registerStampingRecipe(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object stamp, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampingRecipeAction(key, itemInput, fluidInput, fluidInputAmount, stamp, output, outputCount));
	}

	public boolean registerStampingRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object stamp, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampingRecipeAction(key, fluidInput, fluidInputAmount, stamp, output, outputCount));
	}

	public boolean registerStampingRecipe(ResourceLocation key, Object itemInput, Object stamp, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampingRecipeAction(key, itemInput, stamp, output, outputCount));
	}
}
