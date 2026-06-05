package thelm.jaopca.compat.energizedpower.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.OutputItemStackTemplateWithPercentages;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class AlloyFurnaceRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object[] input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final double[] secondChances;
	public final int time;

	public AlloyFurnaceRecipeSerializer(Identifier key, Object[] input, Object output, int outputCount, int time) {
		this(key, input, output, outputCount, ItemStack.EMPTY, DoubleArrays.EMPTY_ARRAY, time);
	}

	public AlloyFurnaceRecipeSerializer(Identifier key, Object[] input, Object output, int outputCount, Object secondOutput, double[] secondChances, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondChances = secondChances;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		List<IngredientWithCount> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < input.length && input[i] instanceof Integer) {
				count = (Integer)input[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputs.add(new IngredientWithCount(ing, count));
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		ItemStackTemplate secondStack = MiscHelper.INSTANCE.getItemStackTemplate(secondOutput, 1);
		if(stack == null && (secondStack == null || secondChances.length == 0)) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+secondOutput);
		}
		AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(stack, new OutputItemStackTemplateWithPercentages(secondStack, secondChances), inputs.toArray(IngredientWithCount[]::new), time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
