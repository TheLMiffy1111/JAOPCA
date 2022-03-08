package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.thermal.core.util.recipes.machine.PressRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.thermalexpansion.ThermalExpansionHelper;
import thelm.jaopca.utils.MiscHelper;

public class PressRecipeSupplier implements Supplier<PressRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object itemOutput;
	public final int itemOutputCount;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int energy;
	public final float experience;

	public PressRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		this(key, input, inputCount, null, 0, itemOutput, itemOutputCount, Fluids.EMPTY, 0, energy, experience);
	}

	public PressRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		this(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, Fluids.EMPTY, 0, energy, experience);
	}

	public PressRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.itemOutput = itemOutput;
		this.itemOutputCount = itemOutputCount;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public PressRecipe get() {
		List<Ingredient> inputs = new ArrayList<>();
		Ingredient ing = ThermalExpansionHelper.INSTANCE.getCountedIngredient(input, inputCount);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		inputs.add(ing);
		if(secondInput != null) {
			Ingredient secondIng = ThermalExpansionHelper.INSTANCE.getCountedIngredient(secondInput, secondInputCount);
			if(secondIng != Ingredient.EMPTY && !secondIng.hasNoMatchingItems()) {
				inputs.add(secondIng);
			}
		}
		List<ItemStack> itemOutputs = Collections.emptyList();
		List<Float> itemChances = Collections.emptyList();
		List<FluidStack> fluidOutputs = Collections.emptyList();
		ItemStack itemStack = MiscHelper.INSTANCE.getItemStack(itemOutput, itemOutputCount);
		if(!itemStack.isEmpty()) {
			itemOutputs = Collections.singletonList(itemStack);
			itemChances = Collections.singletonList(-1F);
		}
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(!fluidStack.isEmpty()) {
			fluidOutputs = Collections.singletonList(fluidStack);
		}
		if(itemOutputs.isEmpty() && fluidOutputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+itemOutput+", "+fluidOutput);
		}
		return new PressRecipe(key, energy, experience, inputs, Collections.emptyList(), itemOutputs, itemChances, fluidOutputs);
	}
}
