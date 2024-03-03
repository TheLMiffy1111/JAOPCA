package thelm.jaopca.compat.railcraft;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.railcraft.recipes.CrusherRecipeSerializer;
import thelm.jaopca.compat.railcraft.recipes.RollingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class RailcraftHelper {

	public static final RailcraftHelper INSTANCE = new RailcraftHelper();

	private RailcraftHelper() {}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSerializer(key, input, time, output));
	}

	public boolean registerRollingRecipe(ResourceLocation key, Object output, int outputCount, int time, Object... input) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RollingRecipeSerializer(key, output, outputCount, time, input));
	}
}
