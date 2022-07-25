package thelm.jaopca.compat.bloodmagic.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class AlchemyTableRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object output;
	public final int count;
	public final int cost;
	public final int time;
	public final int minTier;

	public AlchemyTableRecipeSerializer(ResourceLocation key, Object[] input, Object output, int count, int cost, int time, int minTier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.cost = cost;
		this.time = time;
		this.minTier = minTier;
	}

	@Override
	public JsonElement get() {
		List<Ingredient> inputList = new ArrayList<Ingredient>();
		for(Object in : input) {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == EmptyIngredient.INSTANCE) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputList.add(ing);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "bloodmagic:alchemytable");
		JsonArray inputJson = new JsonArray();
		for(Ingredient ing : inputList) {
			inputJson.add(ing.toJson());
		}
		json.add("input", inputJson);
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));
		json.addProperty("syphon", cost);
		json.addProperty("ticks", time);
		json.addProperty("upgradeLevel", minTier);

		return json;
	}
}
