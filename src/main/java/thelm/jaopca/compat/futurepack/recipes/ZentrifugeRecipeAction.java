package thelm.jaopca.compat.futurepack.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import futurepack.api.ItemPredicates;
import futurepack.common.crafting.FPZentrifugeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.futurepack.FuturepackHelper;
import thelm.jaopca.utils.MiscHelper;

public class ZentrifugeRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object[] output;
	public final int support;
	public final int time;

	public ZentrifugeRecipeAction(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.support = support;
		this.time = time;
	}

	@Override
	public boolean register() {
		ItemPredicates ing = FuturepackHelper.INSTANCE.getItemPredicates(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> outputs = new ArrayList<>();
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
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		ItemStack[] outs = outputs.toArray(new ItemStack[outputs.size()]);
		FPZentrifugeManager.addRecipe(ing, outs, support).setTime(time);
		return true;
	}
}
