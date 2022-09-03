package thelm.jaopca.compat.immersiveengineering;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.immersiveengineering.recipes.ArcFurnaceRecipeSerializer;
import thelm.jaopca.compat.immersiveengineering.recipes.CrusherRecipeSerializer;
import thelm.jaopca.compat.immersiveengineering.recipes.MetalPressRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class ImmersiveEngineeringHelper {

	public static final ImmersiveEngineeringHelper INSTANCE = new ImmersiveEngineeringHelper();

	private ImmersiveEngineeringHelper() {}

	public boolean registerArcFurnaceRecipe(ResourceLocation key, Object[] input, Object slag, int slagCount, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ArcFurnaceRecipeSerializer(key, input, slag, slagCount, output, time, energy));
	}

	public boolean registerArcFurnaceRecipe(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ArcFurnaceRecipeSerializer(key, input, output, time, energy));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object[] output, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSerializer(key, input, output, energy));
	}

	public boolean registerMetalPressRecipe(ResourceLocation key, Object input, int inputCount, Object mold, Object output, int outputCount, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MetalPressRecipeSerializer(key, input, inputCount, mold, output, outputCount, energy));
	}
}
