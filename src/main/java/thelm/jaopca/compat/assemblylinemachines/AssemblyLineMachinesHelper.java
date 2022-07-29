package thelm.jaopca.compat.assemblylinemachines;

import me.haydenb.assemblylinemachines.registry.utils.CountIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.compat.assemblylinemachines.recipes.GrinderRecipeSerializer;
import thelm.jaopca.compat.assemblylinemachines.recipes.PneumaticRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class AssemblyLineMachinesHelper {

	public static final AssemblyLineMachinesHelper INSTANCE = new AssemblyLineMachinesHelper();

	private AssemblyLineMachinesHelper() {}

	public CountIngredient getCountIngredient(Object obj, int count) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(obj);
		return ing == EmptyIngredient.INSTANCE ? null : CountIngredient.of(ing, count);
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, int grinds, int tier, boolean requiresMachine, float doubleChance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, grinds, tier, requiresMachine, doubleChance));
	}

	public boolean registerPneumaticRecipe(ResourceLocation key, Object input, int inputCount, Object mold, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PneumaticRecipeSerializer(key, input, inputCount, mold, output, outputCount, time));
	}
}
