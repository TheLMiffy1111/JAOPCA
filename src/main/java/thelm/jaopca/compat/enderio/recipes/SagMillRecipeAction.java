package thelm.jaopca.compat.enderio.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.enderio.EnderIOHelper;
import thelm.jaopca.utils.MiscHelper;

public class SagMillRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int energy;
	public final RecipeBonusType bonusType;
	public final Object[] output;

	public SagMillRecipeAction(String key, Object input, int energy, String bonusType, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.energy = energy;
		this.bonusType = Objects.requireNonNull(RecipeBonusType.valueOf(bonusType.toUpperCase(Locale.US)));
		this.output = output;
	}

	@Override
	public boolean register() {
		RecipeInput ing = EnderIOHelper.INSTANCE.getRecipeInput(input, 1);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<RecipeOutput> outputs = new ArrayList<>();
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count, false);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", new Object[] {key, out});
				continue;
			}
			outputs.add(new RecipeOutput(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		RecipeOutput[] out = outputs.toArray(new RecipeOutput[outputs.size()]);
		CrusherRecipeManager.getInstance().addRecipe(new Recipe(ing, energy, bonusType, out));
		return true;
	}
}
