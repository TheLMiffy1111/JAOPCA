package thelm.jaopca.compat.indreb.recipes;

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
import thelm.jaopca.ingredients.IntersectionIngredient;
import thelm.jaopca.utils.MiscHelper;

public class CompressingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondChance;
	public final int time;
	public final int power;
	public final float experience;

	public CompressingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		this(key, "", input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power, experience);
	}

	public CompressingRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		this(key, group, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power, experience);
	}

	public CompressingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondCount, float secondChance, int time, int power, float experience) {
		this(key, "", input, inputCount, output, outputCount, secondOutput, secondCount, secondChance, time, power, experience);
	}

	public CompressingRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.time = time;
		this.power = power;
		this.experience = experience;
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
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		if(secondChance > 0 && secondStack.isEmpty()) {
			LOGGER.warn("Empty non-zero chance second output in recipe {}: {}", key, secondOutput);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "indreb:compressing");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		JsonObject ingJson = IntersectionIngredient.of(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", inputCount);
		json.add("ingredient", ingJson);
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("result", resultJson);
		if(!secondStack.isEmpty()) {
			JsonObject secondJson = new JsonObject();
			secondJson.addProperty("item", secondStack.getItem().getRegistryName().toString());
			secondJson.addProperty("count", secondStack.getCount());
			secondJson.addProperty("chance", secondChance);
			json.add("bonus_result", secondJson);
		}
		json.addProperty("experience", experience);
		json.addProperty("duration", time);
		json.addProperty("power_cost", power);

		return json;
	}
}
