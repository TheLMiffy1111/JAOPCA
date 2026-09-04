package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import igentuman.nc.recipe.FluidOutput;
import igentuman.nc.recipe.UniversalProcessorRecipe;
import igentuman.nc.setup.entries.Processors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object[] output;
	public final int time;
	public final int power;

	public CentrifugeRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object[] output, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		SizedFluidIngredient ing = MiscHelper.INSTANCE.getSizedFluidIngredient(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<FluidOutput> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer amount = 1;
			if(i < output.length && output[i] instanceof Integer) {
				amount = (Integer)output[i];
				++i;
			}
			FluidStack stack = MiscHelper.INSTANCE.getFluidStack(out, amount);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(FluidOutput.of(stack.getFluid(), stack.getAmount()));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		UniversalProcessorRecipe recipe = new UniversalProcessorRecipe(Processors.CENTRIFUGE, List.of(), List.of(ing), List.of(), outputs, time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
