package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.recipe.impl.CrushingIRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSupplier implements Supplier<CrushingIRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public CrushingIRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new CrushingIRecipe(key, ItemStackIngredient.from(ing, inputCount), stack);
	}
}
