package thelm.jaopca.compat.immersiveengineering;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.immersiveengineering.recipes.ArcFurnaceRecipeSupplier;
import thelm.jaopca.compat.immersiveengineering.recipes.CrusherRecipeSupplier;
import thelm.jaopca.compat.immersiveengineering.recipes.MetalPressRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class ImmersiveEngineeringHelper {

	public static final ImmersiveEngineeringHelper INSTANCE = new ImmersiveEngineeringHelper();

	private ImmersiveEngineeringHelper() {}

	public boolean registerArcFurnaceRecipe(ResourceLocation key, Object[] input, Object slag, int slagCount, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ArcFurnaceRecipeSupplier(key, input, slag, slagCount, output, time, energy));
	}

	public boolean registerArcFurnaceRecipe(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ArcFurnaceRecipeSupplier(key, input, output, time, energy));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object[] output, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeSupplier(key, input, output, energy));
	}

	public boolean registerMetalPressRecipe(ResourceLocation key, Object input, int inputCount, Object mold, Object output, int outputCount, int energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MetalPressRecipeSupplier(key, input, inputCount, mold, output, outputCount, energy));
	}
}
