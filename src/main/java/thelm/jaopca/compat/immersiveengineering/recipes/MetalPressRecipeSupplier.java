package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class MetalPressRecipeSupplier implements Supplier<MetalPressRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object mold;
	public final Object output;
	public final int outputCount;
	public final int energy;

	public MetalPressRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object mold, Object output, int outputCount, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.mold = mold;
		this.output = output;
		this.outputCount = outputCount;
		this.energy = energy;
	}

	@Override
	public MetalPressRecipe get() {
		IngredientWithSize ing = new IngredientWithSize(MiscHelper.INSTANCE.getIngredient(input), inputCount);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ComparableItemStack moldStack = new ComparableItemStack(MiscHelper.INSTANCE.getItemStack(mold, 1));
		if(moldStack.stack.isEmpty()) {
			throw new IllegalArgumentException("Empty mold in recipe "+key+": "+mold);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new MetalPressRecipe(key, stack, ing, moldStack, energy);
	}
}
