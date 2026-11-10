package thelm.jaopca.compat.thaumaturge;

import java.util.List;

import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;

import net.minecraft.resources.Identifier;
import thelm.jaopca.compat.thaumaturge.recipes.AspectInstance;
import thelm.jaopca.compat.thaumaturge.recipes.CrucibleRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class ThaumaturgeHelper {

	public static final ThaumaturgeHelper INSTANCE = new ThaumaturgeHelper();

	private ThaumaturgeHelper() {}

	public boolean registerCrucibleRecipe(Identifier key, Object input, List<AspectInstance> aspects, Object output, int outputCount, ResearchGate research) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSerializer(key, input, aspects, output, outputCount, research));
	}
}
