package thelm.jaopca.compat.bloodmagic.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.bloodmagic.BloodMagicHelper;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;
import wayoftime.bloodmagic.recipe.helper.FluidStackIngredient;

public class ARCRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object tool;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object[] output;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final boolean consumeInput;

	public ARCRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object tool, Object[] output, boolean consumeInput) {
		this(key, input, inputCount, tool, null, 0, output, null, 0, consumeInput);
	}

	public ARCRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object tool, Object fluidInput, int fluidInputAmount, Object[] output, Object fluidOutput, int fluidOutputAmount, boolean consumeInput) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.tool = tool;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.consumeInput = consumeInput;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Ingredient ingTool = MiscHelper.INSTANCE.getIngredient(tool);
		if(ingTool == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+tool);
		}
		FluidStackIngredient fluidIng = BloodMagicHelper.INSTANCE.getFluidStackIngredient(fluidInput, fluidInputAmount);
		ItemStack result = null;
		List<Pair<ItemStack, Double>> chanceOutputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Double chance = 1D;
			if(i < output.length && output[i] instanceof Double) {
				chance = (Double)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			if(result == null) {
				result = stack;
			}
			else {
				chanceOutputs.add(Pair.of(stack, chance));
			}
		}
		if(result == null) {
			LOGGER.warn("No main output in recipe {}", key);
			result = ItemStack.EMPTY;
		}
		FluidStack fluidResult = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(result.isEmpty() && chanceOutputs.isEmpty() && fluidResult.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output)+", "+fluidOutput);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "bloodmagic:arc");
		json.add("input", ing.toJson());
		json.addProperty("inputsize", inputCount);
		json.add("tool", ingTool.toJson());
		if(fluidIng != null) {
			json.add("inputFluid", fluidIng.serialize());
		}
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(result));
		JsonArray addedJson = new JsonArray();
		for(Pair<ItemStack, Double> pair : chanceOutputs) {
			JsonObject outputJson = new JsonObject();
			outputJson.add("type", MiscHelper.INSTANCE.serializeItemStack(pair.getLeft()));
			if(pair.getRight() >= 1) {
				outputJson.addProperty("mainchance", 1);
				outputJson.addProperty("chance", 0);
			}
			else {
				outputJson.addProperty("mainchance", 0);
				outputJson.addProperty("chance", pair.getRight());
			}
			addedJson.add(outputJson);
		}
		json.add("addedoutput", addedJson);
		if(!fluidResult.isEmpty()) {
			json.add("outputFluid", MiscHelper.INSTANCE.serializeFluidStack(fluidResult));
		}
		json.addProperty("consumeingredient", consumeInput);

		return json;
	}
}
