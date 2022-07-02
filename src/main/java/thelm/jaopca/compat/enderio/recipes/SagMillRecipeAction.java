package thelm.jaopca.compat.enderio.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.enderio.EnderIOHelper;
import thelm.jaopca.utils.MiscHelper;

public class SagMillRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int energy;
	public final RecipeBonusType bonusType;
	public final RecipeLevel level;
	public final Object[] output;

	public SagMillRecipeAction(ResourceLocation key, Object input, int energy, String bonusType, String level, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.energy = energy;
		this.bonusType = Objects.requireNonNull(RecipeBonusType.valueOf(bonusType.toUpperCase(Locale.US)));
		this.level = Objects.requireNonNull(RecipeLevel.valueOf(level.toUpperCase(Locale.US)));
		this.output = output;
	}

	@Override
	public boolean register() {
		Things ing = EnderIOHelper.INSTANCE.getThings(input);
		if(ing.isEmpty()) {
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(new RecipeOutput(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		IRecipeInput in = new ThingsRecipeInput(ing);
		RecipeOutput[] out = outputs.toArray(new RecipeOutput[outputs.size()]);
		SagMillRecipeManager.getInstance().addRecipe(new Recipe(in, energy, bonusType, level, out));
		return true;
	}
}
