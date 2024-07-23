package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.basic.BasicChemicalDissolutionRecipe;
import mekanism.api.recipes.ingredients.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class DissolutionRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object gasInput;
	public final int gasInputAmount;
	public final Object output;
	public final int outputCount;

	public DissolutionRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputAmount, Object output, int outputCount) {
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
		SlurryStack stack = MekanismHelper.INSTANCE.getSlurryStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BasicChemicalDissolutionRecipe recipe = new BasicChemicalDissolutionRecipe(ing, gasIng, stack);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
