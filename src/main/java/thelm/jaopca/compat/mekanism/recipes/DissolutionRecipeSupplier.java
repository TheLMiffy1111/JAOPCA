package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.inputs.GasStackIngredient;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.recipe.impl.ItemStackGasToGasIRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class DissolutionRecipeSupplier implements Supplier<ItemStackGasToGasIRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object gasInput;
	public final int gasInputCount;
	public final Object output;
	public final int outputCount;

	public DissolutionRecipeSupplier(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.gasInput = gasInput;
		this.gasInputCount = gasInputCount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public ItemStackGasToGasIRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		GasStackIngredient gasIng = MekanismHelper.INSTANCE.getGasStackIngredient(gasInput, gasInputCount);
		GasStack stack = MekanismHelper.INSTANCE.getGasStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new ItemStackGasToGasIRecipe(key, ItemStackIngredient.from(ing, itemInputCount), gasIng, stack);
	}
}
