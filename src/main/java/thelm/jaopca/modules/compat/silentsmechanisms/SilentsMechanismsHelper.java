package thelm.jaopca.modules.compat.silentsmechanisms;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;

public class SilentsMechanismsHelper {

	public static final SilentsMechanismsHelper INSTANCE = new SilentsMechanismsHelper();

	private SilentsMechanismsHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return JAOPCAApi.instance().registerRecipe(key, new CrushingRecipeSupplier(key, input, time, output));
	}
}
