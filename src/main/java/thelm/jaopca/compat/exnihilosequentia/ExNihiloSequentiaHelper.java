package thelm.jaopca.compat.exnihilosequentia;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.exnihilosequentia.recipes.SieveRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class ExNihiloSequentiaHelper {

	public static final ExNihiloSequentiaHelper INSTANCE = new ExNihiloSequentiaHelper();

	private ExNihiloSequentiaHelper() {}

	public boolean registerSieveRecipe(ResourceLocation key, Object input, Object output, int outputCount, Number[] meshChances, boolean waterlogged) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SieveRecipeSerializer(key, input, output, outputCount, meshChances, waterlogged));
	}
}
