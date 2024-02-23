package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.NuclearCraftHelper;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object[] output;
	public final double radiation;
	public final double time;
	public final double power;

	public CentrifugeRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object[] output, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.radiation = radiation;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		FluidStackIngredient ing = NuclearCraftHelper.INSTANCE.getFluidStackIngredient(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<FluidStack> outputs = new ArrayList<>();
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
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "nuclearcraft:centrifuge");
		JsonArray inputJson = new JsonArray();
		inputJson.add(ing.serialize());
		json.add("inputFluids", inputJson);
		JsonArray outputJson = new JsonArray();
		for(FluidStack stack : outputs) {
			outputJson.add(MiscHelper.INSTANCE.serializeFluidStack(stack));
		}
		json.add("outputFluids", outputJson);
		json.addProperty("radiation", radiation);
		json.addProperty("timeModifier", time);
		json.addProperty("powerModifier", power);

		return json;
	}
}
