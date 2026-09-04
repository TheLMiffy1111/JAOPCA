package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.NuclearCraftHelper;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class LeacherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputAmount;
	public final double radiation;
	public final double time;
	public final double power;

	public LeacherRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.radiation = radiation;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		Ingredient itemIng = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(itemIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		FluidStackIngredient fluidIng = NuclearCraftHelper.INSTANCE.getFluidStackIngredient(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "nuclearcraft:leacher");
		JsonArray itemInputJson = new JsonArray();
		JsonObject itemIngJson = MiscHelper.INSTANCE.wrapIngredient(itemIng).toJson().getAsJsonObject();
		itemIngJson.addProperty("count", itemInputCount);
		itemInputJson.add(itemIngJson);
		json.add("input", itemInputJson);
		JsonArray fluidInputJson = new JsonArray();
		fluidInputJson.add(fluidIng.serialize());
		json.add("inputFluids", fluidInputJson);
		JsonArray outputJson = new JsonArray();
		outputJson.add(MiscHelper.INSTANCE.serializeFluidStack(stack));
		json.add("outputFluids", outputJson);
		json.addProperty("radiation", radiation);
		json.addProperty("timeModifier", time);
		json.addProperty("powerModifier", power);

		return json;
	}
}
