package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import mekanism.api.recipes.basic.BasicCrushingRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		ItemStackIngredient ing = MekanismHelper.INSTANCE.getItemStackIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BasicCrushingRecipe recipe = new BasicCrushingRecipe(ing, stack);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
