package thelm.jaopca.compat.essentialcraft;

import java.util.function.Consumer;

import ec3.utils.common.EnumOreColoring;
import thelm.jaopca.compat.essentialcraft.recipes.MagmaticSmelterRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class EssentialCraftHelper {

	public static final EssentialCraftHelper INSTANCE = new EssentialCraftHelper();

	private EssentialCraftHelper() {}

	public boolean registerMagmaticSmelterRecipe(String key, String input, String output, int count, Consumer<EnumOreColoring> callback) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MagmaticSmelterRecipeAction(key, input, output, count, callback));
	}
}
