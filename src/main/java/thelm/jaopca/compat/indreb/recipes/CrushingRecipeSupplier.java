package thelm.jaopca.compat.indreb.recipes;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.maciej916.indreb.common.receipe.CrushingRecipe;
import com.maciej916.indreb.common.receipe.basic.ChanceResult;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSupplier implements Supplier<CrushingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondChance;
	public final int time;
	public final int power;

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power) {
		this(key, "", input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power);
	}

	public CrushingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int power) {
		this(key, group, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power);
	}

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondCount, float secondChance, int time, int power) {
		this(key, "", input, inputCount, output, outputCount, secondOutput, secondCount, secondChance, time, power);
	}

	public CrushingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.time = time;
		this.power = power;
	}

	@Override
	public CrushingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		if(secondChance > 0 && secondStack.isEmpty()) {
			LOGGER.warn("Empty non-zero chance second output in recipe {}: {}", key, secondOutput);
		}
		List<ChanceResult> result = Lists.newArrayList(new ChanceResult(secondStack, secondOutputCount, secondChance));
		return new CrushingRecipe(key, ing, inputCount, stack, result, time, power);
	}
}
