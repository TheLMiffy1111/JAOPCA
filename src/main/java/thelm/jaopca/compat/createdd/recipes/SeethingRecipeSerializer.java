package thelm.jaopca.compat.createdd.recipes;

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
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class SeethingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] output;

	public SeethingRecipeSerializer(ResourceLocation key, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<Pair<ItemStack, Float>> outputs = new ArrayList<>();
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(Pair.of(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+Arrays.deepToString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "create_dd:seething");
		JsonArray ingJson = new JsonArray();
		ingJson.add(ing.toJson());
		json.add("ingredients", ingJson);
		JsonArray resultJson = new JsonArray();
		for(Pair<ItemStack, Float> pair : outputs) {
			JsonObject outputJson = MiscHelper.INSTANCE.serializeItemStack(pair.getLeft());
			outputJson.addProperty("chance", pair.getRight());
			resultJson.add(outputJson);
		}
		json.add("results", resultJson);

		return json;
	}
}
