package thelm.jaopca.compat.exnihilosequentia;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.exnihilosequentia.recipes.SieveRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class ExNihiloSequentiaHelper {

	public static final ExNihiloSequentiaHelper INSTANCE = new ExNihiloSequentiaHelper();

	private ExNihiloSequentiaHelper() {}

	public boolean registerSieveRecipe(ResourceLocation key, Object input, Object output, int outputCount, Number[] meshChances, boolean waterlogged) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SieveRecipeSupplier(key, input, output, outputCount, meshChances, waterlogged));
	}
}
