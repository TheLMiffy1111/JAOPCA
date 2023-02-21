package thelm.jaopca.compat.essentialcraft;

import java.util.function.Consumer;

import essentialcraft.api.OreSmeltingRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.essentialcraft.recipes.MagmaticSmelterRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class EssentialCraftHelper {

	public static final EssentialCraftHelper INSTANCE = new EssentialCraftHelper();

	private EssentialCraftHelper() {}

	public boolean registerMagmaticSmelterRecipe(ResourceLocation key, String input, String output, int count, Consumer<OreSmeltingRecipe> callback) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MagmaticSmelterRecipeAction(key, input, output, count, callback));
	}
}
