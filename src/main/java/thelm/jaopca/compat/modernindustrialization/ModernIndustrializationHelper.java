package thelm.jaopca.compat.modernindustrialization;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.modernindustrialization.recipes.CompressorRecipeSerializer;
import thelm.jaopca.compat.modernindustrialization.recipes.CuttingMachineRecipeSerializer;
import thelm.jaopca.compat.modernindustrialization.recipes.ForgeHammerRecipeSerializer;
import thelm.jaopca.compat.modernindustrialization.recipes.ImplosionCompressorRecipeSerializer;
import thelm.jaopca.compat.modernindustrialization.recipes.MaceratorRecipeSerializer;
import thelm.jaopca.compat.modernindustrialization.recipes.PackerRecipeSerializer;
import thelm.jaopca.compat.modernindustrialization.recipes.UnpackerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class ModernIndustrializationHelper {

	public static final ModernIndustrializationHelper INSTANCE = new ModernIndustrializationHelper();

	private ModernIndustrializationHelper() {}

	public boolean registerForgeHammerRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int hammerDamage) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ForgeHammerRecipeSerializer(key, input, inputCount, output, outputCount, hammerDamage));
	}

	public boolean registerMaceratorRecipe(ResourceLocation key, Object input, int inputCount, float inputChance, Object[] output, int eu, int duration) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaceratorRecipeSerializer(key, input, inputCount, inputChance, output, eu, duration));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, int inputCount, float inputChance, Object output, int outputCount, float outputChance, int eu, int duration) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeSerializer(key, input, inputCount, inputChance, output, outputCount, outputChance, eu, duration));
	}

	public boolean registerImplosionCompressorRecipe(ResourceLocation key, Object[] input, Object[] output, int eu, int duration) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressorRecipeSerializer(key, input, output, eu, duration));
	}

	public boolean registerCuttingMachineRecipe(ResourceLocation key, Object itemInput, int itemInputCount, float itemInputChance, Object fluidInput, int fluidInputAmount, float fluidInputChance, Object output, int outputCount, float outputChance, int eu, int duration) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CuttingMachineRecipeSerializer(key, itemInput, itemInputCount, itemInputChance, fluidInput, fluidInputAmount, fluidInputChance, output, outputCount, outputChance, eu, duration));
	}

	public boolean registerPackerRecipe(ResourceLocation key, Object[] input, Object output, int outputCount, float outputChance, int eu, int duration) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PackerRecipeSerializer(key, input, output, outputCount, outputChance, eu, duration));
	}

	public boolean registerUnpackerRecipe(ResourceLocation key, Object input, int inputCount, float inputChance, Object[] output, int eu, int duration) {
		return ApiImpl.INSTANCE.registerRecipe(key, new UnpackerRecipeSerializer(key, input, inputCount, inputChance, output, eu, duration));
	}
}
