package thelm.jaopca.compat.crossroads.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class MillRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object[] output;

	public MillRecipeSerializer(ResourceLocation key, Object input, Object... output) {
		this(key, "", input, output);
	}

	public MillRecipeSerializer(ResourceLocation key, String group, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> stacks = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			stacks.add(stack);
		}
		if(stacks.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "crossroads:mill");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("input", ing.toJson());
		JsonArray resultJson = new JsonArray();
		for(ItemStack stack : stacks) {
			resultJson.add(MiscHelper.INSTANCE.serializeItemStack(stack));
		}
		json.add("output", resultJson);

		return json;
	}
}
