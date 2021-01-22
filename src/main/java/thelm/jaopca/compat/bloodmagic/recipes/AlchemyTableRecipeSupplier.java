package thelm.jaopca.compat.bloodmagic.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;
import wayoftime.bloodmagic.recipe.RecipeAlchemyTable;

public class AlchemyTableRecipeSupplier implements Supplier<RecipeAlchemyTable> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object output;
	public final int count;
	public final int cost;
	public final int time;
	public final int minTier;

	public AlchemyTableRecipeSupplier(ResourceLocation key, Object[] input, Object output, int count, int cost, int time, int minTier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.cost = cost;
		this.time = time;
		this.minTier = minTier;
	}

	@Override
	public RecipeAlchemyTable get() {
		List<Ingredient> inputList = new ArrayList<Ingredient>();
		for(Object in : input) {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing.hasNoMatchingItems()) {
				LOGGER.warn("Empty input in recipe {}: {}", key, in);
			}
			inputList.add(ing);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new RecipeAlchemyTable(key, inputList, stack, cost, time, minTier);
	}
}
