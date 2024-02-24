package thelm.jaopca.compat.techreborn.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import techreborn.api.recipe.RecipeHandler;
import techreborn.api.recipe.machines.GrinderRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object[] input;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object[] output;
	public final int time;
	public final int energy;

	public GrinderRecipeAction(String key, Object[] input, Object fluidInput, int fluidInputAmount, Object[] output, int time, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public boolean register() {
		List<ItemStack> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer inc = 1;
			if(i < input.length && input[i] instanceof Integer) {
				inc = (Integer)input[i];
				++i;
			}
			ItemStack ing = MiscHelper.INSTANCE.getItemStack(in, inc, true);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputs.add(ing);
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		List<ItemStack> outputs = new ArrayList<>();
		i = 0;
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
				LOGGER.warn("Empty output in recipe {}: {}", new Object[] {key, out});
				continue;
			}
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		GrinderRecipe recipe = new GrinderRecipe(null, null, fluidStack, null, null, null, null, time, energy);
		for(ItemStack in : inputs) {
			recipe.inputs.add(in);
		}
		for(ItemStack out : outputs) {
			recipe.addOutput(out);
		}
		RecipeHandler.addRecipe(recipe);
		return true;
	}
}
