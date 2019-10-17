package thelm.jaopca.modules.compat.silentsmechanisms;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.mechanisms.crafting.recipe.CompressingRecipe;
import net.silentchaos512.mechanisms.crafting.recipe.CrushingRecipe;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.utils.MiscHelper;

public class CompressingRecipeSupplier implements Supplier<CompressingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;

	public CompressingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		this.key = key;
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public CompressingRecipe get() {
		Ingredient ing = JAOPCAApi.instance().miscHelper().getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		try {
			CompressingRecipe ret = new CompressingRecipe(key);
			Field processTimeField = CompressingRecipe.class.getDeclaredField("processTime");
			Field ingredientField = CompressingRecipe.class.getDeclaredField("ingredient");
			Field ingredientCountField  = CompressingRecipe.class.getDeclaredField("ingredientCount");
			Field resultField = CompressingRecipe.class.getDeclaredField("result");
			processTimeField.setAccessible(true);
			ingredientField.setAccessible(true);
			resultField.setAccessible(true);
			processTimeField.setInt(ret, time);
			ingredientField.set(ret, ing);
			ingredientCountField.set(ret, inputCount);
			resultField.set(ret, stack);
			return ret;
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
