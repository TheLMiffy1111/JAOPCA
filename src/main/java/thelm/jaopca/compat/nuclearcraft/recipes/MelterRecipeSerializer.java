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
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MelterRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputAmount;
	public final int time;
	public final int power;

	public MelterRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		UniversalProcessorRecipe recipe = new UniversalProcessorRecipe(Processors.MELTER, List.of(ing), List.of(), List.of(), List.of(FluidOutput.of(stack.getFluid(), stack.getAmount())), time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
