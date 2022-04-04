package thelm.jaopca.compat.cyclic.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class CrusherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int secondChance;
	public final int time;
	public final int power;

	public CrusherRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time, int power) {
		this(key, input, output, outputCount, ItemStack.EMPTY, 0, 0, time, power);
	}

	public CrusherRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "cyclic:crusher");
		json.add("input", ing.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("result", resultJson);
		if(!secondStack.isEmpty()) {
			json.addProperty("percent", secondChance);
			JsonObject secondJson = new JsonObject();
			secondJson.addProperty("item", secondStack.getItem().getRegistryName().toString());
			secondJson.addProperty("count", secondStack.getCount());
			json.add("bonus", secondJson);
		}
		JsonObject energyJson = new JsonObject();
		energyJson.addProperty("ticks", time);
		energyJson.addProperty("rfpertick", power);
		json.add("energy", energyJson);

		return json;
	}
}
