package thelm.jaopca.compat.usefulmachinery;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.usefulmachinery.recipes.CompactingRecipeSupplier;
import thelm.jaopca.compat.usefulmachinery.recipes.CrushingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class UsefulMachineryHelper {

	public static final UsefulMachineryHelper INSTANCE = new UsefulMachineryHelper();

	private UsefulMachineryHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, output, count, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, group, input, output, count));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, input, output, count));
	}
}
