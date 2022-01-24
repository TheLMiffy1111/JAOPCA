package thelm.jaopca.compat.occultism;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.occultism.recipes.CrushingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class OccultismHelper {

	public static final OccultismHelper INSTANCE = new OccultismHelper();

	private OccultismHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time, boolean ignoreMultiplier) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, output, outputCount, time, ignoreMultiplier));
	}
}
