package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class ArcFurnaceRecipeSupplier implements Supplier<ArcFurnaceRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object slag;
	public final int slagCount;
	public final Object[] output;
	public final int time;
	public final int energy;

	public ArcFurnaceRecipeSupplier(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		this(key, input, ItemStack.EMPTY, 0, output, time, energy);
	}

	public ArcFurnaceRecipeSupplier(ResourceLocation key, Object[] input, Object slag, int slagCount, Object[] output, int time, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.slag = slag;
		this.slagCount = slagCount;
		this.output = output;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public ArcFurnaceRecipe get() {
		IngredientWithSize ing = null;
		List<IngredientWithSize> additives = new ArrayList<IngredientWithSize>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < input.length && input[i] instanceof Integer) {
				count = (Integer)input[i];
				++i;
			}
			IngredientWithSize is = new IngredientWithSize(MiscHelper.INSTANCE.getIngredient(in), count);
			if(is.hasNoMatchingItems()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			if(ing == null) {
				ing = is;
			}
			else {
				additives.add(is);
			}
		}
		if(ing == null || ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		ItemStack slagStack = MiscHelper.INSTANCE.getItemStack(slag, slagCount);
		NonNullList<ItemStack> outputs = NonNullList.create();
		i = 0;
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
		return new ArcFurnaceRecipe(key, outputs, ing, slagStack, time, energy, additives.toArray(new IngredientWithSize[additives.size()]));
	}
}
