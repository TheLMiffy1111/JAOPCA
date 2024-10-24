package thelm.jaopca.compat.immersiveengineering.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.TagOutputList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ArcFurnaceRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object slag;
	public final int slagCount;
	public final Object[] output;
	public final int time;
	public final int energy;

	public ArcFurnaceRecipeSerializer(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		this(key, input, ItemStack.EMPTY, 0, output, time, energy);
	}

	public ArcFurnaceRecipeSerializer(ResourceLocation key, Object[] input, Object slag, int slagCount, Object[] output, int time, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.slag = slag;
		this.slagCount = slagCount;
		this.output = output;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		IngredientWithSize ing = null;
		List<IngredientWithSize> additives = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < input.length && input[i] instanceof Integer) {
				count = (Integer)input[i];
				++i;
			}
			Ingredient is = MiscHelper.INSTANCE.getIngredient(in);
			if(is == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			if(ing == null) {
				ing = new IngredientWithSize(is, count);
			}
			else {
				additives.add(new IngredientWithSize(is, count));
			}
		}
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		List<TagOutput> outputs = new ArrayList<>();
		List<StackWithChance> secondary = new ArrayList<>();
		i = 0;
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
			if(chance == 1F) {
				outputs.add(new TagOutput(new IngredientWithSize(is, count)));
			}
			else {
				secondary.add(new StackWithChance(new TagOutput(new IngredientWithSize(is, count)), chance));
			}
		}
		if(outputs.isEmpty() && secondary.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		Ingredient slagIng = MiscHelper.INSTANCE.getIngredient(slag);
		TagOutput slagOutput = slagIng == null ? TagOutput.EMPTY : new TagOutput(new IngredientWithSize(slagIng, slagCount));
		ArcFurnaceRecipe recipe = new ArcFurnaceRecipe(new TagOutputList(outputs), slagOutput, secondary, time, energy, ing, additives);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
