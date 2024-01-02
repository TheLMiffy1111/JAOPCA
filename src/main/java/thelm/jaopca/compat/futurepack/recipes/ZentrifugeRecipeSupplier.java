package thelm.jaopca.compat.futurepack.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import futurepack.api.ItemPredicateBase;
import futurepack.common.recipes.centrifuge.ZentrifugeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.futurepack.FuturepackHelper;
import thelm.jaopca.utils.MiscHelper;

public class ZentrifugeRecipeSupplier implements Supplier<ZentrifugeRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object[] output;
	public final int support;
	public final int time;

	public ZentrifugeRecipeSupplier(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.support = support;
		this.time = time;
	}

	@Override
	public ZentrifugeRecipe get() {
		ItemPredicateBase ing = FuturepackHelper.INSTANCE.getItemPredicateBase(input, inputCount);
		if(ing.collectAcceptedItems(new ArrayList<>()).isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> results = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe "+key+": {}", out);
				continue;
			}
			results.add(stack);
		}
		if(results.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in zentrifuge recipe: "+Arrays.deepToString(output));
		}
		ZentrifugeRecipe ret = new ZentrifugeRecipe(ing, results.toArray(new ItemStack[results.size()]), support);
		ret.setTime(time);
		return ret;
	}
}
