package thelm.jaopca.compat.usefulmachinery.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;
import themcbros.usefulmachinery.machine.CompactorMode;
import themcbros.usefulmachinery.recipes.CompactingRecipe;

public class CompactingRecipeSupplier implements Supplier<CompactingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final CompactorMode mode;

	public CompactingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int mode) {
		this(key, "", input, inputCount, output, outputCount, time, mode);
	}

	public CompactingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int mode) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.mode = CompactorMode.byIndex(mode);
	}

	@Override
	public CompactingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new CompactingRecipe(key, group, ing, inputCount, stack, time, mode);
	}
}
