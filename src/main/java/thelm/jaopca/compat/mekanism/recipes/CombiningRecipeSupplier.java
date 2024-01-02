package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.recipe.impl.CombinerIRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CombiningRecipeSupplier implements Supplier<CombinerIRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object output;
	public final int outputCount;

	public CombiningRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public CombinerIRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Ingredient secondIng = MiscHelper.INSTANCE.getIngredient(secondInput);
		if(secondIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+secondInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new CombinerIRecipe(key, ItemStackIngredient.from(ing, inputCount), ItemStackIngredient.from(secondIng, secondInputCount), stack);
	}
}
