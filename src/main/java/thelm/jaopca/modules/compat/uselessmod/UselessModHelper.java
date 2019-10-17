package thelm.jaopca.modules.compat.uselessmod;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;

public class UselessModHelper {

	public static final UselessModHelper INSTANCE = new UselessModHelper();

	private UselessModHelper() {}

	public boolean registerCrusherRecipe(ResourceLocation key, String group, Object input, Object output, int count, Object secondOutput, int secondCount, float secondChance, float experience, int time) {
		return JAOPCAApi.instance().registerRecipe(key, new CrusherRecipeSupplier(key, group, input, output, count, secondOutput, secondCount, secondChance, experience, time));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int count, Object secondOutput, int secondCount, float secondChance, float experience, int time) {
		return JAOPCAApi.instance().registerRecipe(key, new CrusherRecipeSupplier(key, input, output, count, secondOutput, secondCount, secondChance, experience, time));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return JAOPCAApi.instance().registerRecipe(key, new CrusherRecipeSupplier(key, group, input, output, count, experience, time));
	}

	public boolean registerCrusherRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return JAOPCAApi.instance().registerRecipe(key, new CrusherRecipeSupplier(key, input, output, count, experience, time));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return JAOPCAApi.instance().registerRecipe(key, new CompressorRecipeSupplier(key, group, input, output, count, experience, time));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return JAOPCAApi.instance().registerRecipe(key, new CompressorRecipeSupplier(key, input, output, count, experience, time));
	}
}
