package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SmeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation id;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final float experience;
	public final int time;

	public SmeltingRecipeSerializer(ResourceLocation id, Object input, Object output, int count, float experience, int time) {
		this(id, "", input, output, count, experience, time);
	}

	public SmeltingRecipeSerializer(ResourceLocation id, String group, Object input, Object output, int count, float experience, int time) {
		this.id = Objects.requireNonNull(id);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.count = count;
		this.experience = experience;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+id+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", id, output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:smelting");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("ingredient", ing.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("result", resultJson);
		json.addProperty("experience", experience);
		json.addProperty("cookingtime", time);

		return json;
	}
}
