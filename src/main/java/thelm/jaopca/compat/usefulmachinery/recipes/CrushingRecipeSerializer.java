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
import thelm.jaopca.ingredients.EmptyIngredient;
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
	public final float secondOutputChance;
	public final int time;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		this(key, "", input, output, outputCount, ItemStack.EMPTY, 0, 0, time);
	}

	public CrushingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputCount, int time) {
		this(key, group, input, output, outputCount, ItemStack.EMPTY, 0, 0, time);
	}

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time) {
		this(key, "", input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time);
	}

	public CrushingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
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
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "usefulmachinery:crushing");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("ingredient", ing.toJson());
		json.add("result", MiscHelper.INSTANCE.serializeItemStack(stack));
		if(!secondStack.isEmpty()) {
			JsonObject secondaryJson = MiscHelper.INSTANCE.serializeItemStack(stack);
			secondaryJson.addProperty("chance", secondOutputChance);
			json.add("secondary", secondaryJson);
		}
		json.addProperty("processingtime", time);

		return json;
	}
}
