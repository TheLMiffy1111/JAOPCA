package thelm.jaopca.compat.ftbic.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class RollingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final double outputChance;
	public final double time;

	public RollingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time) {
		this(key, input, inputCount, output, outputCount, 1D, time);
	}

	public RollingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double outputChance, double time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.time = time;
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

		JsonObject json = new JsonObject();
		json.addProperty("type", "ftbic:rolling");
		JsonArray itemInputJson = new JsonArray();
		itemInputJson.add(new IngredientWithCount(ing, inputCount).toJson());
		json.add("inputItems", itemInputJson);
		JsonArray itemOutputJson = new JsonArray();
		itemOutputJson.add(new StackWithChance(stack, outputChance).toJson());
		json.add("outputItems", itemOutputJson);
		json.addProperty("processingTime", time);

		return json;
	}
}
