package thelm.jaopca.compat.embers.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.recipe.FluidIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.embers.EmbersHelper;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class StampingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object stamp;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;

	public StampingRecipeSerializer(ResourceLocation key, Object stamp, Object itemInput, Object output, int outputCount) {
		this(key, stamp, itemInput, null, 0, output, outputCount);
	}

	public StampingRecipeSerializer(ResourceLocation key, Object stamp, Object fluidInput, int fluidInputAmount, Object output, int outputCount) {
		this(key, stamp, null, fluidInput, fluidInputAmount, output, outputCount);
	}

	public StampingRecipeSerializer(ResourceLocation key, Object stamp, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.stamp = stamp;
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		Ingredient stampIng = MiscHelper.INSTANCE.getIngredient(stamp);
		if(stampIng == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+stamp);
		}
		Ingredient itemIng = MiscHelper.INSTANCE.getIngredient(itemInput);
		FluidIngredient fluidIng = EmbersHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		if(itemIng == EmptyIngredient.INSTANCE && fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "embers:stamping");
		json.add("stamp", stampIng.toJson());
		if(itemIng != EmptyIngredient.INSTANCE) {
			json.add("input", itemIng.toJson());
		}
		if(fluidIng != null) {
			json.add("fluid", fluidIng.serialize());
		}
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));

		return json;
	}
}
