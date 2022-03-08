package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.thermal.core.util.recipes.machine.PulverizerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.thermalexpansion.ThermalExpansionHelper;
import thelm.jaopca.utils.MiscHelper;

public class PulverizerRecipeSupplier implements Supplier<PulverizerRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object[] output;
	public final int energy;
	public final float experience;
	
	public PulverizerRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object[] output, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public PulverizerRecipe get() {
		Ingredient ing = ThermalExpansionHelper.INSTANCE.getCountedIngredient(input, inputCount);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> outputs = new ArrayList<>();
		List<Float> chances = new ArrayList<>();
		int i = 0;
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
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.toString(output));
		}
		return new PulverizerRecipe(key, energy, experience, Collections.singletonList(ing), Collections.emptyList(), outputs, chances, Collections.emptyList());
	}
}
