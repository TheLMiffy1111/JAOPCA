package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CrusherRecipeSupplier implements Supplier<CrusherRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] output;
	public final int energy;

	public CrusherRecipeSupplier(ResourceLocation key, Object input, Object[] output, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.energy = energy;
	}

	@Override
	public CrusherRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack result = null;
		List<StackWithChance> secondary = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = 1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			if(result == null) {
				result = stack;
				continue;
			}
			else {
				secondary.add(new StackWithChance(stack, chance));
			}
		}
		if(result == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		CrusherRecipe recipe = new CrusherRecipe(key, result, ing, energy);
		for(StackWithChance stack : secondary) {
			recipe.addToSecondaryOutput(stack);
		}
		return recipe;
	}
}
