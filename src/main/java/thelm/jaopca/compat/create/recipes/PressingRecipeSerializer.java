package thelm.jaopca.compat.create.recipes;

import java.util.ArrayList;
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
import thelm.jaopca.utils.MiscHelper;

public class PressingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;

	public PressingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "create:pressing");
		JsonArray ingJson = new JsonArray();
		ingJson.add(ing.toJson());
		json.add("ingredients", ingJson);
		JsonArray resultJson = new JsonArray();
		JsonObject outputJson = new JsonObject();
		outputJson.addProperty("item", stack.getItem().getRegistryName().toString());
		outputJson.addProperty("count", stack.getCount());
		resultJson.add(outputJson);
		json.add("results", resultJson);

		return json;
	}
}
