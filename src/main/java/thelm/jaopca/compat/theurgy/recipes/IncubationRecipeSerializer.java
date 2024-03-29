package thelm.jaopca.compat.theurgy.recipes;

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

public class IncubationRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object mercury;
	public final Object salt;
	public final Object sulfur;
	public final Object output;
	public final int outputCount;
	public final int time;

	public IncubationRecipeSerializer(ResourceLocation key, Object mercury, Object salt, Object sulfur, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.mercury = mercury;
		this.salt = salt;
		this.sulfur = sulfur;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient mercuryIng = MiscHelper.INSTANCE.getIngredient(mercury);
		if(mercuryIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+mercury);
		}
		Ingredient saltIng = MiscHelper.INSTANCE.getIngredient(salt);
		if(saltIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+salt);
		}
		Ingredient sulfurIng = MiscHelper.INSTANCE.getIngredient(sulfur);
		if(sulfurIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+sulfur);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "theurgy:incubation");
		json.add("mercury", mercuryIng.toJson());
		json.add("salt", saltIng.toJson());
		json.add("sulfur", sulfurIng.toJson());
		json.add("result", MiscHelper.INSTANCE.serializeItemStack(stack));
		json.addProperty("incubation_time", time);

		return json;
	}
}
