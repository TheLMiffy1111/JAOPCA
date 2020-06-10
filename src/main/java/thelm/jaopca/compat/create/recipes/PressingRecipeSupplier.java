package thelm.jaopca.compat.create.recipes;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class PressingRecipeSupplier implements Supplier<PressingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int outputCount;

	public PressingRecipeSupplier(ResourceLocation key, Object input, Object output, int outputCount) {
		this(key, "", input, output, outputCount);
	}

	public PressingRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public PressingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.getStack().isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new PressingRecipe(key, group, Collections.singletonList(new ProcessingIngredient(ing)), Collections.singletonList(new ProcessingOutput(stack, 1F)), -1);
	}
}
