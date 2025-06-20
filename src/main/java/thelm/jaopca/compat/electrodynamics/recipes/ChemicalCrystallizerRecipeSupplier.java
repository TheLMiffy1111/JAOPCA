package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import electrodynamics.common.recipe.categories.fluid2item.specificmachines.ChemicalCrystalizerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;
import voltaic.common.recipe.recipeutils.FluidIngredient;

public class ChemicalCrystallizerRecipeSupplier implements Supplier<ChemicalCrystalizerRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final double experience;
	public final int time;
	public final double energy;

	public ChemicalCrystallizerRecipeSupplier(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public ChemicalCrystalizerRecipe get() {
		FluidIngredient ing = ElectrodynamicsHelper.INSTANCE.getFluidIngredient(input, inputAmount);
		if(ing.getMatchingFluids().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new ChemicalCrystalizerRecipe(key, Collections.singletonList(ing), stack, experience, time, energy, Collections.emptyList(), Collections.emptyList());
	}
}
