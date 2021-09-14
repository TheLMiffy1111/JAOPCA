package thelm.jaopca.compat.voluminousenergy.recipes;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.veteam.voluminousenergy.recipe.CompressorRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CompressingRecipeSupplier implements Supplier<CompressorRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;

	public CompressingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public CompressorRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		try {
			CompressorRecipe ret = new CompressorRecipe(key);
			ret.ingredient = ing;
			ret.ingredientCount = inputCount;
			ret.result = stack;
			Field processTimeField = CompressorRecipe.class.getDeclaredField("processTime");
			Field outputAmountField = CompressorRecipe.class.getDeclaredField("outputAmount");
			processTimeField.setAccessible(true);
			outputAmountField.setAccessible(true);
			processTimeField.setInt(ret, time);
			outputAmountField.setInt(ret, outputCount);
			return ret;
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
