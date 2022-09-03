package thelm.jaopca.compat.immersiveengineering.recipes;

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

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class ArcFurnaceRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object slag;
	public final int slagCount;
	public final Object[] output;
	public final int time;
	public final int energy;

	public ArcFurnaceRecipeSerializer(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		this(key, input, ItemStack.EMPTY, 0, output, time, energy);
	}

	public ArcFurnaceRecipeSerializer(ResourceLocation key, Object[] input, Object slag, int slagCount, Object[] output, int time, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.slag = slag;
		this.slagCount = slagCount;
		this.output = output;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		IngredientWithSize ing = null;
		List<IngredientWithSize> additives = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < input.length && input[i] instanceof Integer) {
				count = (Integer)input[i];
				++i;
			}
			Ingredient is = MiscHelper.INSTANCE.getIngredient(in);
			if(is == EmptyIngredient.INSTANCE) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			if(ing == null) {
				ing = new IngredientWithSize(is, count);
			}
			else {
				additives.add(new IngredientWithSize(is, count));
			}
		}
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		IngredientWithSize slagIng = new IngredientWithSize(MiscHelper.INSTANCE.getIngredient(slag), slagCount);
		List<IngredientWithSize> outputs = new ArrayList<>();
		List<Pair<IngredientWithSize, Float>> secondary = new ArrayList<>();
		i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = 1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			Ingredient is = MiscHelper.INSTANCE.getIngredient(out);
			if(is == EmptyIngredient.INSTANCE) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			if(chance == 1F) {
				outputs.add(new IngredientWithSize(is));
			}
			else {
				secondary.add(Pair.of(new IngredientWithSize(is), chance));
			}
		}
		if(outputs.isEmpty() && secondary.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "immersiveengineering:arc_furnace");
		json.add("input", ing.serialize());
		JsonArray addJson = new JsonArray();
		for(IngredientWithSize additive : additives) {
			addJson.add(additive.serialize());
		}
		json.add("additives", addJson);
		JsonArray resultJson = new JsonArray();
		for(IngredientWithSize outIng : outputs) {
			resultJson.add(outIng.serialize());
		}
		json.add("results", resultJson);
		JsonArray secondaryJson = new JsonArray();
		for(Pair<IngredientWithSize, Float> pair : secondary) {
			JsonObject outputJson = new JsonObject();
			outputJson.add("output", pair.getLeft().serialize());
			outputJson.addProperty("chance", pair.getRight());
			secondaryJson.add(outputJson);
		}
		json.add("secondaries", secondaryJson);
		if(!slagIng.hasNoMatchingItems()) {
			json.add("slag", slagIng.serialize());
		}
		json.addProperty("time", time);
		json.addProperty("energy", energy);

		return json;
	}
}
