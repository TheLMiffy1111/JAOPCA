package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class PurifyingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object gasInput;
	public final int gasInputAmount;
	public final Object output;
	public final int outputCount;

	public PurifyingRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.gasInput = gasInput;
		this.gasInputAmount = gasInputAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		ItemStackIngredient ing = MekanismHelper.INSTANCE.getItemStackIngredient(itemInput, itemInputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		GasStackIngredient gasIng = MekanismHelper.INSTANCE.getGasStackIngredient(gasInput, gasInputAmount);
		if(gasIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+gasInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "mekanism:purifying");
		json.add("itemInput", ing.serialize());
		json.add("chemicalInput", gasIng.serialize());
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));

		return json;
	}
}
