package thelm.jaopca.compat.techreborn.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.OreRecipeInput;
import techreborn.api.recipe.machines.IndustrialGrinderRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class IndustrialGrinderRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final int time;
	public final int energy;
	public final Object[] output;

	public IndustrialGrinderRecipeAction(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, int time, int energy, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.time = time;
		this.energy = energy;
		this.output = output;
	}

	@Override
	public boolean register() {
		List<Object> ins = new ArrayList<>();
		if(itemInput instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(itemInput)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
			}
			ins.add(new OreRecipeInput((String)itemInput, itemInputCount));
		}
		else {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
			}
			for(ItemStack is : ing.getMatchingStacks()) {
				ins.add(MiscHelper.INSTANCE.resizeItemStack(is, itemInputCount));
			}
		}
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		if(fluidStack == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
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
		for(Object in : ins) {
			IndustrialGrinderRecipe recipe = new IndustrialGrinderRecipe(in, fluidStack, null, null, null, null, time, energy);
			for(ItemStack out : outputs) {
				recipe.addOutput(out);
			}
			RecipeHandler.addRecipe(recipe);
		}
		return true;
	}

}
