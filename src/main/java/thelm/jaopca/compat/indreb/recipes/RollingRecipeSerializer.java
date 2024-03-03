package thelm.jaopca.compat.indreb.recipes;

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

public class RollingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final int power;
	public final float experience;

	public RollingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		this.key = key;
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
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

		JsonObject json = new JsonObject();
		json.addProperty("type", "indreb:rolling");
		JsonArray ingsJson = new JsonArray();
		JsonObject ingJson = MiscHelper.INSTANCE.wrapIngredient(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", inputCount);
		ingsJson.add(ingJson);
		json.add("ingredients", ingsJson);
		JsonObject resultJson = MiscHelper.INSTANCE.serializeItemStack(stack);
		json.add("result", resultJson);
		json.addProperty("duration", time);
		json.addProperty("tick_energy_cost", power);
		json.addProperty("experience", experience);

		return json;
	}
}
