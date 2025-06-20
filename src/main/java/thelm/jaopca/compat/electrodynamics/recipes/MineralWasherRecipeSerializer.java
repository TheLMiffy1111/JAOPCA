package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MineralWasherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputAmount;
	public final double experience;
	public final int time;
	public final double energy;

	public MineralWasherRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "electrodynamics:mineral_washer_recipe");
		JsonObject itemInputJson = new JsonObject();
		itemInputJson.addProperty("count", 1);
		JsonObject itemIngJson;
		if(itemInput instanceof String || itemInput instanceof ResourceLocation) {
			itemIngJson = new JsonObject();
			itemIngJson.addProperty("tag", itemInput.toString());
			itemIngJson.addProperty("count", itemInputCount);
		}
		else {
			ItemStack ing = MiscHelper.INSTANCE.getItemStack(itemInput, itemInputCount);
			itemIngJson = MiscHelper.INSTANCE.serializeItemStack(ing);
		}
		itemInputJson.add("0", itemIngJson);
		json.add("iteminputs", itemInputJson);
		JsonObject fluidInputJson = new JsonObject();
		fluidInputJson.addProperty("count", 1);
		JsonObject fluidIngJson;
		if(fluidInput instanceof String || fluidInput instanceof ResourceLocation) {
			fluidIngJson = new JsonObject();
			fluidIngJson.addProperty("tag", fluidInput.toString());
			fluidIngJson.addProperty("amount", fluidInputAmount);
		}
		else {
			FluidStack ing = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
			fluidIngJson = MiscHelper.INSTANCE.serializeFluidStack(ing);
		}
		fluidInputJson.add("0", fluidIngJson);
		json.add("fluidinputs", fluidInputJson);
		json.add("output", MiscHelper.INSTANCE.serializeFluidStack(stack));
		json.addProperty("experience", experience);
		json.addProperty("ticks", time);
		json.addProperty("usagepertick", energy);

		return json;
	}
}
