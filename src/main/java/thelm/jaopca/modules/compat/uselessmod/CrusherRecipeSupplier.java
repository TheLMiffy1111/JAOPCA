package thelm.jaopca.modules.compat.uselessmod;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;
import tk.themcbros.uselessmod.recipes.CrusherRecipe;

public class CrusherRecipeSupplier implements Supplier<CrusherRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final Object secondOutput;
	public final int secondCount;
	public final float secondChance;
	public final float experience;
	public final int time;

	public CrusherRecipeSupplier(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		this(key, "", input, output, count, ItemStack.EMPTY, 0, 0, experience, time);
	}

	public CrusherRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		this(key, group, input, output, count, ItemStack.EMPTY, 0, 0, experience, time);
	}

	public CrusherRecipeSupplier(ResourceLocation key, Object input, Object output, int count, Object secondOutput, int secondCount, float secondChance, float experience, int time) {
		this(key, "", input, output, count, secondOutput, secondCount, secondChance, experience, time);
	}

	public CrusherRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int count, Object secondOutput, int secondCount, float secondChance, float experience, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.count = count;
		this.secondOutput = secondOutput;
		this.secondCount = secondCount;
		this.secondChance = secondChance;
		this.experience = experience;
		this.time = time;
	}

	@Override
	public CrusherRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondCount);
		if(secondChance > 0 && secondStack.isEmpty()) {
			LOGGER.warn("Empty non-zero chance second output in recipe {}: {}", key, secondOutput);
		}
		return new CrusherRecipe(key, group, ing, stack, secondStack, secondChance, experience, time);
	}
}
