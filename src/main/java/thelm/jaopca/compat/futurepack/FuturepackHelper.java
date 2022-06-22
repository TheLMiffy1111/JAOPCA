package thelm.jaopca.compat.futurepack;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.futurepack.recipes.CentrifugeRecipeSerializer;

public class FuturepackHelper {

	public static final FuturepackHelper INSTANCE = new FuturepackHelper();

	private FuturepackHelper() {}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		return FuturepackDataInjector.registerRecipe("centrifuge", key, new CentrifugeRecipeSerializer(key, input, inputCount, support, time, output));
	}
}
