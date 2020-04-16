package thelm.jaopca.compat.create.recipes;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.simibubi.create.modules.contraptions.components.press.PressingRecipe;
import com.simibubi.create.modules.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.modules.contraptions.processing.ProcessingOutput;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.CreateHelper;

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
		ProcessingIngredient ing = CreateHelper.INSTANCE.getProcessingIngredient(input);
		if(ing.getIngredient().hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ProcessingOutput stack = CreateHelper.INSTANCE.getProcessingOutput(output, outputCount);
		if(stack.getStack().isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new PressingRecipe(key, group, Collections.singletonList(ing), Collections.singletonList(stack), -1);
	}
}
