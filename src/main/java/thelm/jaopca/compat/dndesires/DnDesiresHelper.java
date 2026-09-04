package thelm.jaopca.compat.dndesires;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.dndesires.recipes.SeethingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class DnDesiresHelper {

	public static final DnDesiresHelper INSTANCE = new DnDesiresHelper();

	private DnDesiresHelper() {}

	public boolean registerSeethingRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SeethingRecipeSerializer(key, input, output));
	}
}
