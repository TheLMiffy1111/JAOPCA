package thelm.jaopca.compat.advancedrocketry;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.advancedrocketry.recipes.SmallPlatePressRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class AdvancedRocketryHelper {

	public static final AdvancedRocketryHelper INSTANCE = new AdvancedRocketryHelper();

	private AdvancedRocketryHelper() {}

	public boolean registerSmallPlatePressRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmallPlatePressRecipeAction(key, input, output, outputCount));
	}
}
