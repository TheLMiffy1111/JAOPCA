package thelm.jaopca.compat.usefulmachinery.recipes;

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

public class CrushingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondChance;
	public final int time;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		this(key, "", input, output, outputCount, ItemStack.EMPTY, 0, 0, time);
	}

	public CrushingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputCount, int time) {
		this(key, group, input, output, outputCount, ItemStack.EMPTY, 0, 0, time);
	}

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondCount, float secondChance, int time) {
		this(key, "", input, output, outputCount, secondOutput, secondCount, secondChance, time);
	}

	public CrushingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(output, outputCount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "usefulmachinery:crushing");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("ingredient", ing.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("result", resultJson);
		if(!secondStack.isEmpty()) {
			JsonObject secondJson = new JsonObject();
			secondJson.addProperty("item", secondStack.getItem().getRegistryName().toString());
			secondJson.addProperty("count", secondStack.getCount());
			secondJson.addProperty("chance", secondChance);
			json.add("secondary", secondJson);
		}
		json.addProperty("processingtime", time);

		return json;
	}
}
