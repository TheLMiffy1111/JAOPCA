package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicWashingRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class WashingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object chemicalInput;
	public final int chemicalInputAmount;
	public final Object output;
	public final int outputCount;

	public WashingRecipeSerializer(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object chemicalInput, int chemicalInputAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.chemicalInput = chemicalInput;
		this.chemicalInputAmount = chemicalInputAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		FluidStackIngredient fluidIng = MekanismHelper.INSTANCE.getFluidStackIngredient(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		ChemicalStackIngredient chemicalIng = MekanismHelper.INSTANCE.getChemicalStackIngredient(chemicalInput, chemicalInputAmount);
		if(chemicalIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+chemicalInput);
		}
		ChemicalStack stack = MekanismHelper.INSTANCE.getChemicalStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BasicWashingRecipe recipe = new BasicWashingRecipe(fluidIng, chemicalIng, stack);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
