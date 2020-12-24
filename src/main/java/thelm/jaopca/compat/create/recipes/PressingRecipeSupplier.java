package thelm.jaopca.compat.create.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class PressingRecipeSupplier implements Supplier<PressingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;

	public PressingRecipeSupplier(ResourceLocation key, Object input, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public PressingRecipe get() {
		ProcessingRecipeBuilder<PressingRecipe> builder = new ProcessingRecipeBuilder<>(PressingRecipe::new, key);
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		builder.require(ing);
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.getStack().isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		builder.output(stack);
		return builder.build();
	}
}
