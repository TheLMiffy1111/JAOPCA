package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.NuclearCraftHelper;
import thelm.jaopca.utils.MiscHelper;

public class IngotFormerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final double radiation;
	public final double time;
	public final double power;
	
	public IngotFormerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.radiation = radiation;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		FluidStackIngredient ing = NuclearCraftHelper.INSTANCE.getFluidStackIngredient(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "nuclearcraft:ingot_former");
		JsonArray inputJson = new JsonArray();
		inputJson.add(ing.serialize());
		json.add("inputFluids", inputJson);
		JsonArray outputJson = new JsonArray();
		outputJson.add(MiscHelper.INSTANCE.serializeItemStack(stack));
		json.add("output", outputJson);
		json.addProperty("radiation", radiation);
		json.addProperty("timeModifier", time);
		json.addProperty("powerModifier", power);
		
		return json;
	}
}
