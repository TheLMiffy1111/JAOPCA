package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CrusherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] output;
	public final int energy;

	public CrusherRecipeSerializer(ResourceLocation key, Object input, Object[] output, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		TagOutput result = null;
		List<StackWithChance> secondary = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = 1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			Ingredient is = MiscHelper.INSTANCE.getIngredient(out);
			if(is == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			if(result == null) {
				result = new TagOutput(new IngredientWithSize(is, count));
			}
			else {
				secondary.add(new StackWithChance(new TagOutput(new IngredientWithSize(is, count)), chance));
			}
		}
		if(result == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		CrusherRecipe recipe = new CrusherRecipe(result, ing, energy, secondary);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
