package thelm.jaopca.compat.create.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.melonstudios.createlegacy.recipe.WashingRecipes;
import com.melonstudios.createlegacy.util.RecipeEntry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class WashingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] output;

	public WashingRecipeAction(ResourceLocation key, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<RecipeEntry> outputs = new ArrayList<>();
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(RecipeEntry.get(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+Arrays.deepToString(output));
		}
		RecipeEntry[] out = outputs.toArray(new RecipeEntry[outputs.size()]);
		for(ItemStack in : ing.getMatchingStacks()) {
			WashingRecipes.addRecipe(in, out);
		}
		return true;
	}
}
