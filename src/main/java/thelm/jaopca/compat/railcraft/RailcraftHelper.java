package thelm.jaopca.compat.railcraft;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.railcraft.recipes.RockCrusherRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class RailcraftHelper {

	public static final RailcraftHelper INSTANCE = new RailcraftHelper();

	private RailcraftHelper() {}

	public boolean registerRockCrusherRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RockCrusherRecipeAction(key, input, time, output));
	}
}
