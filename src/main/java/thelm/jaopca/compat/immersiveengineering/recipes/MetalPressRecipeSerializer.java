package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MetalPressRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object mold;
	public final Object output;
	public final int outputCount;
	public final int energy;

	public MetalPressRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object mold, Object output, int outputCount, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.mold = mold;
		this.output = output;
		this.outputCount = outputCount;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		IngredientWithSize ing = new IngredientWithSize(MiscHelper.INSTANCE.getIngredient(input), inputCount);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack moldStack = MiscHelper.INSTANCE.getItemStack(mold, 1);
		if(moldStack.isEmpty()) {
			throw new IllegalArgumentException("Empty mold in recipe "+key+": "+mold);
		}
		IngredientWithSize outIng = new IngredientWithSize(MiscHelper.INSTANCE.getIngredient(output), outputCount);
		if(outIng.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "immersiveengineering:metal_press");
		json.add("input", ing.serialize());
		json.addProperty("mold", moldStack.getItem().getRegistryName().toString());
		json.add("result", outIng.serialize());
		json.addProperty("energy", energy);

		return json;
	}
}
