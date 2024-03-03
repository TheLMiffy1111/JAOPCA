package thelm.jaopca.compat.thermalexpansion.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cofh.lib.common.fluid.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.thermalexpansion.ThermalExpansionHelper;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class ChillerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final int energy;
	public final float experience;

	public ChillerRecipeSerializer(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, int energy, float experience) {
		this(key, fluidInput, fluidInputAmount, itemInput, itemInputCount, output, outputCount, -1, energy, experience);
	}

	public ChillerRecipeSerializer(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, float outputChance, int energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public JsonElement get() {
		FluidIngredient fluidIng = ThermalExpansionHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(fluidIng == null && ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+fluidInput+", "+itemInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "thermal:chiller");
		JsonArray ingsJson = new JsonArray();
		if(fluidIng != null) {
			ingsJson.add(fluidIng.toJson());
		}
		if(ing != EmptyIngredient.INSTANCE) {
			JsonObject ingJson = new JsonObject();
			ingJson.add("value", ing.toJson());
			ingJson.addProperty("count", itemInputCount);
			ingsJson.add(ingJson);
		}
		json.add("ingredients", ingsJson);
		JsonArray resultJson = new JsonArray();
		JsonObject itemResultJson = MiscHelper.INSTANCE.serializeItemStack(stack);
		itemResultJson.addProperty("chance", outputChance);
		resultJson.add(itemResultJson);
		json.add("result", resultJson);
		json.addProperty("energy", energy);
		json.addProperty("experience", experience);

		return json;
	}
}
