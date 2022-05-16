package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ChemicalCrystallizerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final double experience;

	public ChemicalCrystallizerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.experience = experience;
	}

	@Override
	public JsonElement get() {
		if(input == Fluids.EMPTY || inputAmount <= 0) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "electrodynamics:chemical_crystallizer_recipe");
		JsonObject fluidInputJson = new JsonObject();
		fluidInputJson.addProperty("count", 1);
		JsonObject ingJson = new JsonObject();
		if(input instanceof String || input instanceof ResourceLocation) {
			ingJson.addProperty("tag", input.toString());
		}
		else if(input instanceof Fluid) {
			ingJson.addProperty("fluid", ((Fluid)input).getRegistryName().toString());
		}
		ingJson.addProperty("amount", inputAmount);
		fluidInputJson.add("0", ingJson);
		json.add("fluidinputs", fluidInputJson);
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));
		json.addProperty("experience", experience);

		return json;
	}
}
