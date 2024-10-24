package thelm.jaopca.compat.createdd;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.createdd.recipes.SeethingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class CreateDDHelper {

	public static final CreateDDHelper INSTANCE = new CreateDDHelper();

	private CreateDDHelper() {}

	public boolean registerSeethingRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SeethingRecipeSerializer(key, input, output));
	}
}
