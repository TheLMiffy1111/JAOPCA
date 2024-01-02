package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.lib.fluid.FluidIngredient;
import cofh.thermal.core.util.recipes.machine.ChillerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.thermalexpansion.ThermalExpansionHelper;
import thelm.jaopca.utils.MiscHelper;

public class ChillerRecipeSupplier implements Supplier<ChillerRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object output;
	public final int outputCount;
	public final int energy;
	public final float experience;

	public ChillerRecipeSupplier(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public ChillerRecipe get() {
		List<Ingredient> itemInputs = new ArrayList<>();
		Ingredient ing = ThermalExpansionHelper.INSTANCE.getCountedIngredient(itemInput, itemInputCount);
		if(ing != Ingredient.EMPTY && !ing.isEmpty()) {
			itemInputs.add(ing);
		}
		FluidIngredient fluidIng = ThermalExpansionHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		if(fluidIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new ChillerRecipe(key, energy, experience, itemInputs, Collections.singletonList(fluidIng), Collections.singletonList(stack), Collections.singletonList(-1F), Collections.emptyList());
	}
}
