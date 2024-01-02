package thelm.jaopca.compat.indreb.recipes;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.maciej916.indreb.common.receipe.RecipeChanceResult;
import com.maciej916.indreb.common.receipe.impl.CrushingRecipe;
import com.maciej916.indreb.common.util.Cache;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSupplier implements Supplier<CrushingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondChance;
	public final int time;
	public final int power;
	public final float experience;

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		this(key, "", input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power, experience);
	}

	public CrushingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int power, float experience) {
		this(key, group, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, power, experience);
	}

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondCount, float secondChance, int time, int power, float experience) {
		this(key, "", input, inputCount, output, outputCount, secondOutput, secondCount, secondChance, time, power, experience);
	}

	public CrushingRecipeSupplier(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int power, float experience) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.time = time;
		this.power = power;
		this.experience = experience;
	}

	@Override
	public CrushingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		try {
			CrushingRecipe ret = new CrushingRecipe(key);
			Field ingredientField = CrushingRecipe.class.getDeclaredField("ingredient");
			Field ingredientCountField = CrushingRecipe.class.getDeclaredField("ingredientCount");
			Field ingredientListField = CrushingRecipe.class.getDeclaredField("ingredientList");
			Field resultField = CrushingRecipe.class.getDeclaredField("result");
			Field bonusResultField = CrushingRecipe.class.getDeclaredField("bonusResult");
			Field durationField = CrushingRecipe.class.getDeclaredField("duration");
			Field powerCostField = CrushingRecipe.class.getDeclaredField("powerCost");
			Field experienceField = CrushingRecipe.class.getDeclaredField("experience");
			ingredientField.setAccessible(true);
			ingredientCountField.setAccessible(true);
			ingredientListField.setAccessible(true);
			resultField.setAccessible(true);
			bonusResultField.setAccessible(true);
			durationField.setAccessible(true);
			powerCostField.setAccessible(true);
			experienceField.setAccessible(true);
			ingredientField.set(ret, ing);
			ingredientCountField.setInt(ret, inputCount);
			ingredientListField.set(ret, Cache.create(()->NonNullList.of(ing)));
			resultField.set(ret, stack);
			((RecipeChanceResult)bonusResultField.get(ret)).addChanceResult(secondStack, secondChance);
			durationField.setInt(ret, time);
			powerCostField.setInt(ret, power);
			experienceField.setFloat(ret, experience);
			return ret;
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
