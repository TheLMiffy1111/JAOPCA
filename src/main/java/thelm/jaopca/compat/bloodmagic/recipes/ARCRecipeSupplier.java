package thelm.jaopca.compat.bloodmagic.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.bloodmagic.BloodMagicHelper;
import thelm.jaopca.utils.MiscHelper;
import wayoftime.bloodmagic.recipe.RecipeARC;
import wayoftime.bloodmagic.recipe.helper.FluidStackIngredient;

public class ARCRecipeSupplier implements Supplier<RecipeARC> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object tool;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object[] output;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final boolean consumeInput;

	public ARCRecipeSupplier(ResourceLocation key, Object input, Object tool, Object[] output, boolean consumeInput) {
		this(key, input, tool, null, 0, output, null, 0, consumeInput);
	}

	public ARCRecipeSupplier(ResourceLocation key, Object input, Object tool, Object fluidInput, int fluidInputAmount, Object[] output, Object fluidOutput, int fluidOutputAmount, boolean consumeInput) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.tool = tool;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.consumeInput = consumeInput;
	}

	@Override
	public RecipeARC get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Ingredient ingTool = MiscHelper.INSTANCE.getIngredient(tool);
		if(ingTool.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+tool);
		}
		FluidStackIngredient fluidIng = BloodMagicHelper.INSTANCE.getFluidStackIngredient(fluidInput, fluidInputAmount);
		ItemStack result = null;
		List<Pair<ItemStack, Double>> chanceOutputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Double chance = 1D;
			if(i < output.length && output[i] instanceof Double) {
				chance = (Double)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			if(result == null) {
				result = stack;
			}
			else {
				chanceOutputs.add(Pair.of(stack, chance));
			}
		}
		if(result == null) {
			LOGGER.warn("No output in recipe {}", key);
			result = ItemStack.EMPTY;
		}
		FluidStack fluidResult = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		return new RecipeARC(key, ing, ingTool, fluidIng, result, chanceOutputs, fluidResult, consumeInput);
	}
}
