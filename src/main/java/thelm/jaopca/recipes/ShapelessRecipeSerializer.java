package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class ShapelessRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapelessRecipeSerializer(ResourceLocation key, Object output, int count, Object... input) {
		this(key, "", output, count, input);
	}

	public ShapelessRecipeSerializer(ResourceLocation key, String group, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public JsonElement get() {
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for(Object in : input) {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == EmptyIngredient.INSTANCE) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			else {
				ingredients.add(ing);
			}
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:crafting_shapeless");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		JsonArray ingJson = new JsonArray();
		for(Ingredient ingredient : ingredients) {
			ingJson.add(ingredient.toJson());
		}
		json.add("ingredients", ingJson);
		json.add("result", MiscHelper.INSTANCE.serializeItemStack(stack));

		return json;
	}
}
