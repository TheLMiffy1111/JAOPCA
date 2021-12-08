package thelm.jaopca.compat.voluminousenergy.recipes;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.veteam.voluminousenergy.recipe.CrusherRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSupplier implements Supplier<CrusherRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondOutputChance;
	public final int time;

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		this(key, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time);
	}

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
		this.time = time;
	}

	@Override
	public CrusherRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		if(secondOutput != ItemStack.EMPTY && secondStack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, secondOutput);
		}
		try {
			CrusherRecipe ret = new CrusherRecipe(key);
			ret.ingredient = ing;
			ret.ingredientCount = inputCount;
			ret.result = stack;
			ret.rngResult = secondStack;
			Field processTimeField = CrusherRecipe.class.getDeclaredField("processTime");
			Field outputAmountField = CrusherRecipe.class.getDeclaredField("outputAmount");
			Field outputRngAmountField = CrusherRecipe.class.getDeclaredField("outputRngAmount");
			Field chanceField = CrusherRecipe.class.getDeclaredField("chance");
			processTimeField.setAccessible(true);
			outputAmountField.setAccessible(true);
			outputRngAmountField.setAccessible(true);
			chanceField.setAccessible(true);
			processTimeField.setInt(ret, time);
			outputAmountField.setInt(ret, outputCount);
			outputRngAmountField.setInt(ret, secondOutputCount);
			chanceField.setFloat(ret, secondOutputChance);
			return ret;
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
