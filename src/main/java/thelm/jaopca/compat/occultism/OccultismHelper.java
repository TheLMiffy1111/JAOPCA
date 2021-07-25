package thelm.jaopca.compat.occultism;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.occultism.recipes.CrushingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class OccultismHelper {

	public static final OccultismHelper INSTANCE = new OccultismHelper();

	private OccultismHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time, boolean ignoreMultiplier) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, output, outputCount, time, ignoreMultiplier));
	}
}
