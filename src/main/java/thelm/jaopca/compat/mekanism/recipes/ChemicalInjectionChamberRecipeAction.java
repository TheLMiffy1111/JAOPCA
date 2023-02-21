package thelm.jaopca.compat.mekanism.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class ChemicalInjectionChamberRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object gasInput;
	public final Object output;
	public final int outputCount;

	public ChemicalInjectionChamberRecipeAction(String key, Object input, int inputCount, Object gasInput, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.gasInput = gasInput;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, inputCount, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		GasStack gasIng = MekanismHelper.INSTANCE.getGasStack(gasInput, 1);
		if(gasIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+gasInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack in : ing) {
			RecipeHandler.addChemicalInjectionChamberRecipe(in, gasIng.getGas().getName(), stack);
		}
		return true;
	}
}
