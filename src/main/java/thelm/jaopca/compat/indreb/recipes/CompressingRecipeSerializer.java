package thelm.jaopca.compat.indreb.recipes;

import java.util.Objects;

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

public class CompressingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
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
		this(key, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power, experience);
	}

	public CompressingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		this.key = Objects.requireNonNull(key);
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
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "indreb:compressing");
		JsonArray ingsJson = new JsonArray();
		JsonObject ingJson = MiscHelper.INSTANCE.wrapIngredient(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", inputCount);
		ingsJson.add(ingJson);
		json.add("ingredients", ingsJson);
		JsonObject resultJson = MiscHelper.INSTANCE.serializeItemStack(stack);
		json.add("result", resultJson);
		JsonArray chanceJson = new JsonArray();
		if(!secondStack.isEmpty()) {
			JsonObject secondJson = MiscHelper.INSTANCE.serializeItemStack(secondStack);
			secondJson.addProperty("chance", secondChance);
			chanceJson.add(secondJson);
		}
		json.add("chance_result", chanceJson);
		json.addProperty("duration", time);
		json.addProperty("tick_energy_cost", power);
		json.addProperty("experience", experience);

		return json;
	}
}
