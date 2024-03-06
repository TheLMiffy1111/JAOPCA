package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.basic.BasicFluidSlurryToSlurryRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.SlurryStackIngredient;
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
	public final Object slurryInput;
	public final int slurryInputAmount;
	public final Object output;
	public final int outputCount;

	public WashingRecipeSerializer(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object slurryInput, int slurryInputAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.slurryInput = slurryInput;
		this.slurryInputAmount = slurryInputAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		FluidStackIngredient fluidIng = MekanismHelper.INSTANCE.getFluidStackIngredient(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		SlurryStackIngredient slurryIng = MekanismHelper.INSTANCE.getSlurryStackIngredient(slurryInput, slurryInputAmount);
		if(slurryIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+slurryInput);
		}
		SlurryStack stack = MekanismHelper.INSTANCE.getSlurryStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BasicFluidSlurryToSlurryRecipe recipe = new BasicFluidSlurryToSlurryRecipe(fluidIng, slurryIng, stack);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
