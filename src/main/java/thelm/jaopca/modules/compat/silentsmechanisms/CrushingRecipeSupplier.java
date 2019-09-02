package thelm.jaopca.modules.compat.silentsmechanisms;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.mechanisms.crafting.recipe.CrushingRecipe;
import thelm.jaopca.api.JAOPCAApi;

public class CrushingRecipeSupplier implements Supplier<CrushingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] output;
	public final int time;

	public CrushingRecipeSupplier(ResourceLocation key, Object input, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.time = time;
	}

	@Override
	public CrushingRecipe get() {
		Ingredient ing = JAOPCAApi.instance().miscHelper().getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Map<ItemStack, Float> results = new LinkedHashMap<>();
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
			ItemStack stack = JAOPCAApi.instance().miscHelper().getStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			results.put(stack, chance);
		}
		try {
			CrushingRecipe ret = new CrushingRecipe(key);
			Field processTimeField = CrushingRecipe.class.getDeclaredField("processTime");
			Field ingredientField = CrushingRecipe.class.getDeclaredField("ingredient");
			Field resultsField = CrushingRecipe.class.getDeclaredField("results");
			processTimeField.setAccessible(true);
			ingredientField.setAccessible(true);
			resultsField.setAccessible(true);
			processTimeField.setInt(ret, time);
			ingredientField.set(ret, ing);
			resultsField.set(ret, results);
			return ret;
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
