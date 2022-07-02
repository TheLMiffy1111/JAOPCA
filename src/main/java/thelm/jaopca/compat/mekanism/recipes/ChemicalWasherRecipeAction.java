package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.MekanismAPI;
import mekanism.api.gas.GasStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.mekanism.MekanismHelper;

public class ChemicalWasherRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputAmount;

	public ChemicalWasherRecipeAction(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
	}

	@Override
	public boolean register() {
		GasStack ing = MekanismHelper.INSTANCE.getGasStack(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		GasStack stack = MekanismHelper.INSTANCE.getGasStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		MekanismAPI.recipeHelper().addChemicalWasherRecipe(ing, stack);
		return true;
	}
}
