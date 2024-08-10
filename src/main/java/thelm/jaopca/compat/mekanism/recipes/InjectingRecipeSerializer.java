package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import mekanism.api.recipes.basic.BasicInjectingRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class InjectingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object chemicalInput;
	public final int chemicalInputAmount;
	public final boolean perTickUsage;
	public final Object output;
	public final int outputCount;

	public InjectingRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object chemicalInput, int chemicalInputAmount, boolean perTickUsage, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.chemicalInput = chemicalInput;
		this.chemicalInputAmount = chemicalInputAmount;
		this.perTickUsage = perTickUsage;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		ItemStackIngredient ing = MekanismHelper.INSTANCE.getItemStackIngredient(itemInput, itemInputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		ChemicalStackIngredient chemicalIng = MekanismHelper.INSTANCE.getChemicalStackIngredient(chemicalInput, chemicalInputAmount);
		if(chemicalIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+chemicalInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BasicInjectingRecipe recipe = new BasicInjectingRecipe(ing, chemicalIng, stack, perTickUsage);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
