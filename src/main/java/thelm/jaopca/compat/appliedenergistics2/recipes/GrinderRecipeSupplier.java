package thelm.jaopca.compat.appliedenergistics2.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import appeng.core.AEConfig;
import appeng.recipes.handlers.GrinderOptionalResult;
import appeng.recipes.handlers.GrinderRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeSupplier implements Supplier<GrinderRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int turns;
	public final Object[] extraOutputs;

	public GrinderRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int turns, Object... extraOutputs) {
		this(key, "", input, inputCount, output, outputCount, turns, extraOutputs);
	}

	public GrinderRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int turns, Object... extraOutputs) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.turns = turns;
		this.extraOutputs = extraOutputs;
	}

	@Override
	public GrinderRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<GrinderOptionalResult> extras = new ArrayList<>();
		int i = 0;
		while(i < extraOutputs.length) {
			Object out = extraOutputs[i];
			++i;
			Integer count = 1;
			if(i < extraOutputs.length && extraOutputs[i] instanceof Integer) {
				count = (Integer)extraOutputs[i];
				++i;
			}
			Float chance = AEConfig.instance().getOreDoublePercentage() / 100F;
			if(i < extraOutputs.length && extraOutputs[i] instanceof Float) {
				chance = (Float)extraOutputs[i];
				++i;
			}
			ItemStack extraStack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(extraStack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			extras.add(new GrinderOptionalResult(chance, extraStack));
		}
		return new GrinderRecipe(key, group, ing, inputCount, stack, turns, extras);
	}
}
