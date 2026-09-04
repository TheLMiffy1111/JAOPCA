package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import igentuman.nc.recipe.FluidOutput;
import igentuman.nc.recipe.UniversalProcessorRecipe;
import igentuman.nc.setup.entries.Processors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class LeacherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputAmount;
	public final int time;
	public final int power;

	public LeacherRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		SizedIngredient itemIng = MiscHelper.INSTANCE.getSizedIngredient(itemInput, itemInputCount);
		if(itemIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		SizedFluidIngredient fluidIng = MiscHelper.INSTANCE.getSizedFluidIngredient(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		UniversalProcessorRecipe recipe = new UniversalProcessorRecipe(Processors.LEACHER, List.of(itemIng), List.of(fluidIng), List.of(), List.of(FluidOutput.of(stack.getFluid(), stack.getAmount())), time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
