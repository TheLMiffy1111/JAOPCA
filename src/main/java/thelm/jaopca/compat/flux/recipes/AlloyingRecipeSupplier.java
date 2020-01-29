package thelm.jaopca.compat.flux.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import szewek.flux.recipe.AlloyingRecipe;
import szewek.flux.recipe.MachineRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class AlloyingRecipeSupplier implements Supplier<AlloyingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object output;
	public final int outputCount;
	public final float experience;
	public final int time;

	public AlloyingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		this(key, "", input, inputCount, Ingredient.EMPTY, 0, output, outputCount, experience, time);
	}

	public AlloyingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		this(key, group, input, inputCount, Ingredient.EMPTY, 0, output, outputCount, experience, time);
	}

	public AlloyingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		this(key, "", input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time);
	}

	public AlloyingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.experience = experience;
		this.time = time;
	}

	@Override
	public AlloyingRecipe get() {
		MachineRecipeSerializer.Builder builder = new MachineRecipeSerializer.Builder();
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		IntList inputCounts = new IntArrayList(2);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		builder.ingredients.add(ing);
		inputCounts.add(inputCount);
		if(secondInputCount > 0) {
			Ingredient secondIng = MiscHelper.INSTANCE.getIngredient(secondInput);
			if(secondIng.hasNoMatchingItems()) {
				LOGGER.warn("Empty ingredient in recipe {}: {}", key, secondIng);
			}
			else {
				builder.ingredients.add(secondIng);
				inputCounts.add(secondInputCount);
			}
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		//builder.itemCost = inputCounts;
		builder.result = stack;
		builder.experience = experience;
		builder.process = time;
		return new AlloyingRecipe(key, group, builder);
	}
}
