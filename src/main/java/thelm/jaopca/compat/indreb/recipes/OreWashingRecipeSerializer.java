package thelm.jaopca.compat.indreb.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class OreWashingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int time;
	public final int power;
	public final float experience;

	public OreWashingRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time, int power, float experience) {
		this(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputCount, ItemStack.EMPTY, 0, time, power, experience);
	}

	public OreWashingRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time, int power, float experience) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.time = time;
		this.power = power;
		this.experience = experience;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		FluidStack fluidIng = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		if(fluidIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "indreb:ore_washing");
		JsonObject ingJson = IntersectionIngredient.of(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", itemInputCount);
		json.add("ingredient", ingJson);
		json.add("fluid_ingredient", MiscHelper.INSTANCE.serializeFluidStack(fluidIng));
		JsonObject resultJson = MiscHelper.INSTANCE.serializeItemStack(stack);
		json.add("result_1", resultJson);
		if(!secondStack.isEmpty()) {
			JsonObject secondJson = MiscHelper.INSTANCE.serializeItemStack(secondStack);
			json.add("result_2", secondJson);
		}
		json.addProperty("duration", time);
		json.addProperty("power_cost", power);
		json.addProperty("experience", experience);

		return json;
	}
}
