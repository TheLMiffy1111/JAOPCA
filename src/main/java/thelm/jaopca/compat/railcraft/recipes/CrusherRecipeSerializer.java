package thelm.jaopca.compat.railcraft.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class CrusherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int time;
	public final Object[] output;

	public CrusherRecipeSerializer(ResourceLocation key, Object input, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.time = time;
		this.output = Objects.requireNonNull(output);
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<Triple<Ingredient, Integer, Float>> outputs = new ArrayList<>();
		int i = 0;
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
			outputs.add(Triple.of(is, count, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "railcraft:crusher");
		json.add("ingredient", ing.toJson());
		JsonArray outputJson = new JsonArray();
		for(Triple<Ingredient, Integer, Float> triple : outputs) {
			JsonObject resultJson = new JsonObject();
			resultJson.add("result", triple.getLeft().toJson());
			resultJson.addProperty("count", triple.getMiddle());
			resultJson.addProperty("probability", triple.getRight());
			outputJson.add(resultJson);
		}
		json.add("outputs", outputJson);
		json.addProperty("processTime", time);

		return json;
	}
}
