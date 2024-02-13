package thelm.jaopca.compat.ganysnether.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ganymedes01.ganysnether.recipes.MagmaticCentrifugeRecipes;
import ganymedes01.ganysnether.recipes.RecipeInput;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.ganysnether.GanysNetherHelper;
import thelm.jaopca.utils.MiscHelper;

public class MagmaticCentrifugeRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object firstInput;
	public final int firstInputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object[] output;

	public MagmaticCentrifugeRecipeAction(String key, Object firstInput, int firstInputCount, Object secondInput, int secondInputCount, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.firstInput = firstInput;
		this.firstInputCount = firstInputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.output = output;
	}

	@Override
	public boolean register() {
		RecipeInput<?> ing1 = GanysNetherHelper.INSTANCE.getRecipeInput(firstInput, firstInputCount);
		if(ing1 == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+firstInput);
		}
		RecipeInput<?> ing2 = GanysNetherHelper.INSTANCE.getRecipeInput(secondInput, secondInputCount);
		if(ing2 == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+secondInput);
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count, false);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		ItemStack[] outs = outputs.toArray(new ItemStack[outputs.size()]);
		MagmaticCentrifugeRecipes.INSTANCE.addRecipe(ing1, ing2, outs);
		return true;
	}
}
