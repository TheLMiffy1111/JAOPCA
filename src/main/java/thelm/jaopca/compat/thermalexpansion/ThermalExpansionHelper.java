package thelm.jaopca.compat.thermalexpansion;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.thermalexpansion.recipes.ChillerRecipeSupplier;
import thelm.jaopca.compat.thermalexpansion.recipes.PressRecipeSupplier;
import thelm.jaopca.compat.thermalexpansion.recipes.PulverizerRecipeSupplier;
import thelm.jaopca.compat.thermalexpansion.recipes.SmelterRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ThermalExpansionHelper {

	public static final ThermalExpansionHelper INSTANCE = new ThermalExpansionHelper();

	private ThermalExpansionHelper() {}

	public Ingredient getCountedIngredient(Object obj, int count) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(obj);
		for(ItemStack stack : ing.getMatchingStacks()) {
			stack.setCount(count);
		}
		return ing;
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, int inputCount, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSupplier(key, input, inputCount, output, energy, experience));
	}

	public boolean registerSmelterRecipe(ResourceLocation key, Object[] input, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmelterRecipeSupplier(key, input, output, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSupplier(key, input, inputCount, itemOutput, itemOutputCount, energy, experience));
	}

	public boolean registerChillerRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChillerRecipeSupplier(key, fluidInput, fluidInputAmount, itemInput, itemInputCount, output, outputCount, energy, experience));
	}
}
