package thelm.jaopca.compat.hbmntm;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.hbmntm.recipes.AnvilConstructionRecipeAction;
import thelm.jaopca.compat.hbmntm.recipes.CentrifugeRecipeAction;
import thelm.jaopca.compat.hbmntm.recipes.CrystallizerRecipeAction;
import thelm.jaopca.compat.hbmntm.recipes.ShredderRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class HBMNTMHelper {

	public static final HBMNTMHelper INSTANCE = new HBMNTMHelper();

	private HBMNTMHelper() {}

	public boolean registerCrystallizerRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CrystallizerRecipeAction(key, input, output, count));
	}

	public boolean registerShredderRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new ShredderRecipeAction(key, input, output, count));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CentrifugeRecipeAction(key, input, output));
	}

	public boolean registerAnvilConstructionRecipe(ResourceLocation key, Object[] input, Object[] output, int tier) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new AnvilConstructionRecipeAction(key, input, output, tier));
	}
}
