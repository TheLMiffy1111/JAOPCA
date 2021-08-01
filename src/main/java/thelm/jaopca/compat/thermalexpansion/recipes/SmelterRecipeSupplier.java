package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.thermal.core.util.recipes.machine.SmelterRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.thermalexpansion.ThermalExpansionHelper;
import thelm.jaopca.utils.MiscHelper;

public class SmelterRecipeSupplier implements Supplier<SmelterRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object[] output;
	public final int energy;
	public final float experience;

	public SmelterRecipeSupplier(ResourceLocation key, Object[] input, Object[] output, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public SmelterRecipe get() {
		List<Ingredient> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Ingredient ing = ThermalExpansionHelper.INSTANCE.getCountedIngredient(in, count);
			if(ing.hasNoMatchingItems()) {
				LOGGER.warn("Empty ingredient in recipe {}: {}", key, in);
			}
			inputs.add(ing);
		}
		List<ItemStack> outputs = new ArrayList<>();
		List<Float> chances = new ArrayList<>();
		i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = -1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			outputs.add(stack);
			chances.add(chance);
		}
		return new SmelterRecipe(key, energy, experience, inputs, Collections.emptyList(), outputs, chances, Collections.emptyList());
	}
}
