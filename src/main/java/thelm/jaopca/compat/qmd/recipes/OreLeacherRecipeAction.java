package thelm.jaopca.compat.qmd.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lach_01298.qmd.recipes.QMDRecipes;
import nc.recipe.ingredient.ChanceItemIngredient;
import nc.recipe.ingredient.EmptyFluidIngredient;
import nc.recipe.ingredient.EmptyItemIngredient;
import nc.recipe.ingredient.IChanceItemIngredient;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.nuclearcraft.NuclearCraftHelper;

public class OreLeacherRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object[] fluidInput;
	public final Object[] output;

	public OreLeacherRecipeAction(ResourceLocation key, Object itemInput, int itemInputCount, Object[] fluidInput, Object[] output) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.output = output;
	}

	@Override
	public boolean register() {
		IItemIngredient itemIng = NuclearCraftHelper.INSTANCE.getItemIngredient(itemInput, itemInputCount);
		List<IFluidIngredient> fluidIngs = new ArrayList<>(3);
		int i = 0;
		while(i < fluidInput.length) {
			Object in = fluidInput[i];
			++i;
			Integer amount = 1000;
			if(i < fluidInput.length && fluidInput[i] instanceof Integer) {
				amount = (Integer)fluidInput[i];
				++i;
			}
			IFluidIngredient ing = NuclearCraftHelper.INSTANCE.getFluidIngredient(in, amount);
			if(ing instanceof EmptyFluidIngredient) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			fluidIngs.add(ing);
		}
		if(itemIng instanceof EmptyItemIngredient && fluidIngs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+Arrays.toString(fluidInput));
		}
		while(fluidIngs.size() < 3) {
			fluidIngs.add(new EmptyFluidIngredient());
		}
		List<IItemIngredient> outputs = new ArrayList<>(3);
		i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer maxCount = 1;
			if(i < output.length && output[i] instanceof Integer) {
				maxCount = (Integer)output[i];
				++i;
			}
			Integer chance = 100;
			if(i < output.length && output[i] instanceof Integer) {
				chance = (Integer)output[i];
				++i;
			}
			Integer minCount = 0;
			if(chance != 100 && i < output.length && output[i] instanceof Integer) {
				minCount = (Integer)output[i];
				++i;
			}
			IItemIngredient ing = NuclearCraftHelper.INSTANCE.getItemIngredient(out, maxCount);
			if(ing instanceof EmptyItemIngredient) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(chance == 100 ? ing : new ChanceItemIngredient(ing, chance, minCount));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.toString(output));
		}
		while(outputs.size() < 3) {
			outputs.add(new EmptyItemIngredient());
		}
		QMDRecipes.ore_leacher.addRecipe(new Object[] {
				itemIng,
				fluidIngs.get(0), fluidIngs.get(1), fluidIngs.get(2),
				outputs.get(0), outputs.get(1), outputs.get(2),
		});
		return true;
	}
}
