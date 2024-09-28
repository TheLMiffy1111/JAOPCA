package thelm.jaopca.compat.enderio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.enderio.recipes.SagMillingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class EnderIOHelper {

	public static final EnderIOHelper INSTANCE = new EnderIOHelper();
	private static final Logger LOGGER = LogManager.getLogger();

	public boolean registerSagMillingRecipe(ResourceLocation key, Object input, int energy, String bonusType, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SagMillingRecipeSerializer(key, input, energy, bonusType, output));
	}
}
