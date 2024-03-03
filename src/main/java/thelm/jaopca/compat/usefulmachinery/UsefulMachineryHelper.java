package thelm.jaopca.compat.usefulmachinery;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.usefulmachinery.recipes.CompactingRecipeSerializer;
import thelm.jaopca.compat.usefulmachinery.recipes.CrushingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class UsefulMachineryHelper {

	public static final UsefulMachineryHelper INSTANCE = new UsefulMachineryHelper();

	private UsefulMachineryHelper() {}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, group, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, String group, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, group, input, output, outputCount, time));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, String mode) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSerializer(key, group, input, inputCount, output, outputCount, time, mode));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, String mode) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSerializer(key, input, inputCount, output, outputCount, time, mode));
	}
}
