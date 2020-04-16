package thelm.jaopca.compat.create.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.simibubi.create.modules.contraptions.components.fan.SplashingRecipe;
import com.simibubi.create.modules.contraptions.components.millstone.MillingRecipe;
import com.simibubi.create.modules.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.modules.contraptions.processing.ProcessingOutput;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.create.CreateHelper;

public class SplashingRecipeSupplier implements Supplier<SplashingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object[] output;

	public SplashingRecipeSupplier(ResourceLocation key, Object input, Object... output) {
		this(key, "", input, output);
	}

	public SplashingRecipeSupplier(ResourceLocation key, String group, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
	}

	@Override
	public SplashingRecipe get() {
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
		return new SplashingRecipe(key, group, Collections.singletonList(ing), results, -1);
	}
}
