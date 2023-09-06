package thelm.jaopca.compat.energizedpower.recipes;

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

public class PulverizerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final double[] chances;
	public final Object secondOutput;
	public final double[] secondChances;

	public PulverizerRecipeSerializer(ResourceLocation key, Object input, Object output, double[] chances) {
		this(key, input, output, chances, ItemStack.EMPTY, new double[0]);
	}

	public PulverizerRecipeSerializer(ResourceLocation key, Object input, Object output, double[] chances, Object secondOutput, double[] secondChances) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.chances = chances;
		this.secondOutput = secondOutput;
		this.secondChances = secondChances;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, 1);
		if(stack.isEmpty() || chances.length == 0) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, 1);

		JsonObject json = new JsonObject();
		json.addProperty("type", "energizedpower:pulverizer");
		json.add("ingredient", ing.toJson());
		JsonObject outputJson = new JsonObject();
		outputJson.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));
		JsonArray outputChanceJson = new JsonArray();
		for(double chance : chances) {
			outputChanceJson.add(chance);
		}
		outputJson.add("percentages", outputChanceJson);
		json.add("output", outputJson);
		if(!secondStack.isEmpty() && secondChances.length != 0) {
			JsonObject secondOutputJson = new JsonObject();
			secondOutputJson.add("output", MiscHelper.INSTANCE.serializeItemStack(secondStack));
			JsonArray secondOutputChanceJson = new JsonArray();
			for(double chance : chances) {
				secondOutputChanceJson.add(chance);
			}
			secondOutputJson.add("percentages", secondOutputChanceJson);
			json.add("secondaryOutput", secondOutputJson);
		}

		return json;
	}
}
