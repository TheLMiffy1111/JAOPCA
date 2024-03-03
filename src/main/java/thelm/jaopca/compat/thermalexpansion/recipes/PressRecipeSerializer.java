package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class PressRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object itemOutput;
	public final int itemOutputCount;
	public final float itemOutputChance;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int energy;
	public final float experience;

	public PressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		this(key, input, inputCount, null, 0, itemOutput, itemOutputCount, -1, Fluids.EMPTY, 0, energy, experience);
	}

	public PressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, float itemOutputChance, int energy, float experience) {
		this(key, input, inputCount, null, 0, itemOutput, itemOutputCount, itemOutputChance, Fluids.EMPTY, 0, energy, experience);
	}

	public PressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		this(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, -1, Fluids.EMPTY, 0, energy, experience);
	}

	public PressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, float itemOutputChance, int energy, float experience) {
		this(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, itemOutputChance, Fluids.EMPTY, 0, energy, experience);
	}

	public PressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		this(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, -1, fluidOutput, fluidOutputAmount, energy, experience);
	}

	public PressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, float itemOutputChance, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.itemOutput = itemOutput;
		this.itemOutputCount = itemOutputCount;
		this.itemOutputChance = itemOutputChance;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Ingredient secondIng = MiscHelper.INSTANCE.getIngredient(secondInput);
		ItemStack itemStack = MiscHelper.INSTANCE.getItemStack(itemOutput, itemOutputCount);
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(itemStack.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+itemOutput+", "+fluidOutput);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "thermal:press");
		JsonArray ingsJson = new JsonArray();
		JsonObject ingJson = new JsonObject();
		ingJson.add("value", ing.toJson());
		ingJson.addProperty("count", inputCount);
		ingsJson.add(ingJson);
		if(secondIng != EmptyIngredient.INSTANCE) {
			JsonObject secondIngJson = new JsonObject();
			secondIngJson.add("value", secondIng.toJson());
			secondIngJson.addProperty("count", secondInputCount);
			ingsJson.add(secondIngJson);
		}
		json.add("ingredients", ingsJson);
		JsonArray resultJson = new JsonArray();
		if(!itemStack.isEmpty()) {
			JsonObject itemResultJson = MiscHelper.INSTANCE.serializeItemStack(itemStack);
			itemResultJson.addProperty("chance", itemOutputChance);
			resultJson.add(itemResultJson);
		}
		if(!fluidStack.isEmpty()) {
			resultJson.add(MiscHelper.INSTANCE.serializeFluidStack(fluidStack));
		}
		json.add("result", resultJson);
		json.addProperty("energy", energy);
		json.addProperty("experience", experience);

		return json;
	}
}
