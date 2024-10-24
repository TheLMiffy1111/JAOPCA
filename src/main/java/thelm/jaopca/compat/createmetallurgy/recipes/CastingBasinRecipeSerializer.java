package thelm.jaopca.compat.createmetallurgy.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.create.CreateHelper;
import thelm.jaopca.compat.createmetallurgy.CreateMetallurgyHelper;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class CastingBasinRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object mold;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final boolean consumeMold;

	public CastingBasinRecipeSerializer(ResourceLocation key, Object mold, Object input, int inputAmount, Object output, int outputCount, int time, boolean consumeMold) {
		this.key = Objects.requireNonNull(key);
		this.mold = mold;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.consumeMold = consumeMold;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(mold);
		FluidIngredient fluidIng = CreateHelper.INSTANCE.getFluidIngredient(input, inputAmount);
		if(fluidIng == null && ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+mold+", "+input);
		}
		CastingOutput out = CreateMetallurgyHelper.INSTANCE.getCastingOutput(output, outputCount);
		if(out == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "createmetallurgy:casting_in_basin");
		JsonArray ingJson = new JsonArray();
		if(ing != EmptyIngredient.INSTANCE) {
			ingJson.add(ing.toJson());
			json.addProperty("mold_consumed", consumeMold);
		}
		if(fluidIng != null) {
			ingJson.add(fluidIng.serialize());
		}
		json.add("ingredients", ingJson);
		json.add("result", out.serialize());
		json.addProperty("processingTime", time);

		return json;
	}
}
