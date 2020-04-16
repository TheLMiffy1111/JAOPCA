package thelm.jaopca.compat.create.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.simibubi.create.modules.contraptions.components.millstone.MillingRecipe;
import com.simibubi.create.modules.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.modules.contraptions.processing.ProcessingOutput;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.CreateHelper;

public class MillingRecipeSupplier implements Supplier<MillingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object[] output;
	public final int time;

	public MillingRecipeSupplier(ResourceLocation key, Object input, int time, Object... output) {
		this(key, "", input, time, output);
	}

	public MillingRecipeSupplier(ResourceLocation key, String group, Object input, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.time = time;
	}

	@Override
	public MillingRecipe get() {
		ProcessingIngredient ing = CreateHelper.INSTANCE.getProcessingIngredient(input);
		if(ing.getIngredient().hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ProcessingOutput> results = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
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
			ProcessingOutput stack = CreateHelper.INSTANCE.getProcessingOutput(out, count, chance);
			if(stack.getStack().isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			results.add(stack);
		}
		return new MillingRecipe(key, group, Collections.singletonList(ing), results, time);
	}
}
