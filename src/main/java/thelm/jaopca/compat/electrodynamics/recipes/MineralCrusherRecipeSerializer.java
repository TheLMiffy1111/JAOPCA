package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class MineralCrusherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final double secondChance;
	public final double experience;
	public final int time;
	public final double energy;

	public MineralCrusherRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		this(key, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, experience, time, energy);
	}

	public MineralCrusherRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
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
		json.addProperty("type", "electrodynamics:mineral_crusher_recipe");
		JsonObject itemInputJson = new JsonObject();
		itemInputJson.addProperty("count", 1);
		JsonObject ingJson = IntersectionIngredient.of(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", inputCount);
		itemInputJson.add("0", ingJson);
		json.add("iteminputs", itemInputJson);
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));
		if(!secondStack.isEmpty()) {
			JsonObject secondJson = new JsonObject();
			secondJson.addProperty("count", 1);
			JsonObject secondResultJson = MiscHelper.INSTANCE.serializeItemStack(secondStack);
			secondResultJson.addProperty("chance", secondChance);
			secondJson.add("0", secondResultJson);
			json.add("itembi", secondJson);
		}
		json.addProperty("experience", experience);
		json.addProperty("ticks", time);
		json.addProperty("usagepertick", energy);

		return json;
	}
}
