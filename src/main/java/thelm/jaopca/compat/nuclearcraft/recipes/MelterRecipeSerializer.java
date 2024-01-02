package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class MelterRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputAmount;
	public final double radiation;
	public final double time;
	public final double power;
	
	public MelterRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.radiation = radiation;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "nuclearcraft:melter");
		JsonArray inputJson = new JsonArray();
		JsonObject ingJson = IntersectionIngredient.of(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", inputCount);
		inputJson.add(ingJson);
		json.add("input", inputJson);
		JsonArray outputJson = new JsonArray();
		outputJson.add(MiscHelper.INSTANCE.serializeFluidStack(stack));
		json.add("outputFluids", outputJson);
		json.addProperty("radiation", radiation);
		json.addProperty("timeModifier", time);
		json.addProperty("powerModifier", power);
		
		return json;
	}
}
