package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class SmelterRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object[] output;
	public final int energy;
	public final float experience;

	public SmelterRecipeSerializer(ResourceLocation key, Object[] input, Object[] output, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public JsonElement get() {
		List<Pair<Ingredient, Integer>> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == EmptyIngredient.INSTANCE) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputs.add(Pair.of(ing, count));
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.toString(input));
		}
		List<Pair<ItemStack, Float>> outputs = new ArrayList<>();
		i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = -1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(Pair.of(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.toString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "thermal:smelter");
		JsonArray ingsJson = new JsonArray();
		for(Pair<Ingredient, Integer> in : inputs) {
			JsonObject ingJson = IntersectionIngredient.of(in.getLeft()).toJson().getAsJsonObject();
			ingJson.addProperty("count", in.getRight());
			ingsJson.add(ingJson);
		}
		json.add("ingredients", ingsJson);
		JsonArray resultJson = new JsonArray();
		for(Pair<ItemStack, Float> out : outputs) {
			JsonObject itemResultJson = MiscHelper.INSTANCE.serializeItemStack(out.getLeft());
			itemResultJson.addProperty("chance", out.getRight());
			resultJson.add(itemResultJson);
		}
		json.add("result", resultJson);
		json.addProperty("energy", energy);
		json.addProperty("experience", experience);

		return json;
	}
}
