package thelm.jaopca.compat.nuclearcraft;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.nuclearcraft.recipes.CentrifugeRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.CrystallizerRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.IngotFormerRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.LeacherRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.ManufactoryRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.MelterRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.PressurizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class NuclearCraftHelper {

	public static final NuclearCraftHelper INSTANCE = new NuclearCraftHelper();

	private NuclearCraftHelper() {}

	public boolean registerLeacherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LeacherRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, time, power));
	}

	public boolean registerManufactoryRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ManufactoryRecipeSerializer(key, input, inputCount, output, outputCount, time, power));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputAmount, Object[] output, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, inputAmount, output, time, power));
	}

	public boolean registerCrystallizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizerRecipeSerializer(key, input, inputAmount, output, outputCount, time, power));
	}

	public boolean registerIngotFormerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotFormerRecipeSerializer(key, input, inputAmount, output, outputCount, time, power));
	}

	public boolean registerMelterRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MelterRecipeSerializer(key, input, inputCount, output, outputAmount, time, power));
	}

	public boolean registerPressurizerRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressurizerRecipeSerializer(key, input, inputCount, output, outputCount, time, power));
	}
}
