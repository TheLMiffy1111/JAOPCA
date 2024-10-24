package thelm.jaopca.compat.createmetallurgy.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.create.CreateHelper;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object secondFluidInput;
	public final int secondFluidInputAmount;
	public final Object itemOutput;
	public final int itemOutputCount;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int time;
	public final int heatLevel;

	public MeltingRecipeSerializer(ResourceLocation key, Object itemInput, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		this(key, itemInput, FluidStack.EMPTY, 0, FluidStack.EMPTY, 0, ItemStack.EMPTY, 0, fluidOutput, fluidOutputAmount, time, heatLevel);
	}

	public MeltingRecipeSerializer(ResourceLocation key, Object itemInput, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		this(key, itemInput, FluidStack.EMPTY, 0, FluidStack.EMPTY, 0, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, time, heatLevel);
	}

	public MeltingRecipeSerializer(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object secondFluidInput, int secondFluidInputAmount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.secondFluidInput = secondFluidInput;
		this.secondFluidInputAmount = secondFluidInputAmount;
		this.itemOutput = itemOutput;
		this.itemOutputCount = itemOutputCount;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.time = time;
		this.heatLevel = heatLevel;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		FluidIngredient fluidIng1 = CreateHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		FluidIngredient fluidIng2 = CreateHelper.INSTANCE.getFluidIngredient(secondFluidInput, secondFluidInputAmount);
		if(ing == EmptyIngredient.INSTANCE && fluidIng1 == null && fluidIng2 == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput+", "+secondFluidInput+", ");
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(itemOutput, itemOutputCount);
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(stack.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+itemOutput+", "+fluidOutput);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "createmetallurgy:melting");
		JsonArray ingJson = new JsonArray();
		if(ing != EmptyIngredient.INSTANCE) {
			ingJson.add(ing.toJson());
		}
		if(fluidIng1 != null) {
			ingJson.add(fluidIng1.serialize());
		}
		if(fluidIng2 != null) {
			ingJson.add(fluidIng2.serialize());
		}
		json.add("ingredients", ingJson);
		JsonArray resultJson = new JsonArray();
		if(!stack.isEmpty()) {
			resultJson.add(MiscHelper.INSTANCE.serializeItemStack(stack));
		}
		if(!fluidStack.isEmpty()) {
			resultJson.add(MiscHelper.INSTANCE.serializeFluidStack(fluidStack));
		}
		json.add("results", resultJson);
		json.addProperty("processingTime", time);
		json.addProperty("heatRequirement", switch(heatLevel) {
		default -> "none";
		case 1 -> "heated";
		case 2 -> "superheated";
		});

		return json;
	}
}
