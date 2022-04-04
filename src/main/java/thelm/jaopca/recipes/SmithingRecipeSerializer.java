package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class SmithingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object base;
	public final Object addition;
	public final Object output;
	public final int count;

	public SmithingRecipeSerializer(ResourceLocation key, Object base, Object addition, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.base = base;
		this.addition = addition;
		this.output = output;
		this.count = count;
	}

	@Override
	public JsonElement get() {
		Ingredient baseIng = MiscHelper.INSTANCE.getIngredient(base);
		if(baseIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+base);
		}
		Ingredient additionIng = MiscHelper.INSTANCE.getIngredient(addition);
		if(additionIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+addition);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:smithing");
		json.add("base", baseIng.toJson());
		json.add("addition", additionIng.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("result", resultJson);

		return json;
	}
}
