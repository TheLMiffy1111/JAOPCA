package thelm.jaopca.compat.minestrapp;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.minestrapp.recipes.CrusherRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class MinestrappHelper {

	public static final MinestrappHelper INSTANCE = new MinestrappHelper();

	private MinestrappHelper() {}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondOutputChance, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, experience));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int outputCount, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, output, outputCount, experience));
	}
}
