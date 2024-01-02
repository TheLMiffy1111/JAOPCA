package thelm.jaopca.compat.wtbwmachines.recipes;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wtbw.mods.lib.util.rand.ItemStackChanceMap;
import com.wtbw.mods.machines.recipe.CrushingRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSupplier implements Supplier<CrushingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object[] output;
	public final int time;
	public final int energy;

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, int time, int energy, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public CrushingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackChanceMap results = new ItemStackChanceMap();
		int i = 0;
		int j = 0;
		while(i < output.length && j < 6) {
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, 1);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			results.add(chance, count, stack);
			++j;
		}
		if(results.getChanceMap().isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		return new CrushingRecipe(key, ing, inputCount, results, time, energy);
	}
}
