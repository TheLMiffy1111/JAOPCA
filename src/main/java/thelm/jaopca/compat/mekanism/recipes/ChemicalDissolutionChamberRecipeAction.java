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

public class ChemicalDissolutionChamberRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputAmount;

	public ChemicalDissolutionChamberRecipeAction(String key, Object input, int inputCount, Object output, int outputAmount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputAmount = outputAmount;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, inputCount, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		GasStack stack = MekanismHelper.INSTANCE.getGasStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack in : ing) {
			RecipeHandler.addChemicalDissolutionChamberRecipe(in, stack);
		}
		return true;
	}
}
