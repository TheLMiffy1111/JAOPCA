package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
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
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack moldStack = MiscHelper.INSTANCE.getItemStack(mold, 1);
		if(moldStack.isEmpty()) {
			throw new IllegalArgumentException("Empty mold in recipe "+key+": "+mold);
		}
		Ingredient outIng = MiscHelper.INSTANCE.getIngredient(output);
		if(outIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "immersiveengineering:metal_press");
		json.add("input", new IngredientWithSize(ing, inputCount).serialize());
		json.addProperty("mold", ForgeRegistries.ITEMS.getKey(moldStack.getItem()).toString());
		json.add("result", new IngredientWithSize(outIng, outputCount).serialize());
		json.addProperty("energy", energy);

		return json;
	}
}
