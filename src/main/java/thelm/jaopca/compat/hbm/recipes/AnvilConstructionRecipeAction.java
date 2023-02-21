package thelm.jaopca.compat.hbm.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.anvil.AnvilRecipes;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.hbm.HBMHelper;
import thelm.jaopca.utils.MiscHelper;

public class AnvilConstructionRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object[] input;
	public final Object[] output;
	public final int tier;

	public AnvilConstructionRecipeAction(String key, Object[] input, Object[] output, int tier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.tier = tier;
	}

	@Override
	public boolean register() {
		List<RecipesCommon.AStack> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			List<RecipesCommon.AStack> list = new ArrayList<>();
			RecipesCommon.AStack ing = HBMHelper.INSTANCE.getAStack(in, count);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputs.add(ing);
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		List<AnvilRecipes.AnvilOutput> outputs = new ArrayList<>();
		i = 0;
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
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(new AnvilRecipes.AnvilOutput(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		AnvilRecipes.AnvilOutput[] outs = outputs.toArray(new AnvilRecipes.AnvilOutput[outputs.size()]);
		RecipesCommon.AStack[] ins = inputs.toArray(new RecipesCommon.AStack[inputs.size()]);
		AnvilRecipes.AnvilConstructionRecipe recipe = new AnvilRecipes.AnvilConstructionRecipe(ins, outs).setTier(tier);
		if(ins.length == 1 && outs.length == 1) {
			recipe.setOverlay(AnvilRecipes.OverlayType.SMITHING);
		}
		else if(ins.length == 1) {
			recipe.setOverlay(AnvilRecipes.OverlayType.RECYCLING);
		}
		else if(outs.length == 1) {
			recipe.setOverlay(AnvilRecipes.OverlayType.CONSTRUCTION);
		}
		AnvilRecipes.getConstruction().add(recipe);
		return true;
	}
}
