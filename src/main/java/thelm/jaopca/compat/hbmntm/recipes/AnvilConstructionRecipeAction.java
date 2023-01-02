package thelm.jaopca.compat.hbmntm.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.hbm.inventory.AnvilRecipes;
import com.hbm.inventory.RecipesCommon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class AnvilConstructionRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object[] output;
	public final int tier;

	public AnvilConstructionRecipeAction(ResourceLocation key, Object[] input, Object[] output, int tier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.tier = tier;
	}

	@Override
	public boolean register() {
		List<List<RecipesCommon.AStack>> inputs = new ArrayList<>();
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
			if(in instanceof String) {
				if(!ApiImpl.INSTANCE.getOredict().contains(in)) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
				}
				RecipesCommon.AStack aStack = new RecipesCommon.OreDictStack((String)in);
				aStack.setCount(count);
				list.add(aStack);
			}
			else {
				Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
				if(ing == null) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
				}
				for(ItemStack is : ing.getMatchingStacks()) {
					RecipesCommon.AStack aStack = new RecipesCommon.ComparableStack(is);
					aStack.setCount(count);
					list.add(aStack);
				}
			}
			inputs.add(list);
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(new AnvilRecipes.AnvilOutput(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		AnvilRecipes.AnvilOutput[] outs = outputs.toArray(new AnvilRecipes.AnvilOutput[outputs.size()]);
		for(List<RecipesCommon.AStack> in : Lists.cartesianProduct(inputs)) {
			RecipesCommon.AStack[] ins = in.toArray(new RecipesCommon.AStack[in.size()]);
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
		}
		return true;
	}
}