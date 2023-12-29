package thelm.jaopca.compat.nuclearcraft;

import java.util.function.Supplier;

import nc.recipe.RecipeHelper;
import nc.recipe.ingredient.EmptyFluidIngredient;
import nc.recipe.ingredient.EmptyItemIngredient;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;

public class NuclearCraftHelper {

	public static final NuclearCraftHelper INSTANCE = new NuclearCraftHelper();

	private NuclearCraftHelper() {}

	public IItemIngredient getItemIngredient(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemIngredient((Supplier<?>)obj, count);
		}
		IItemIngredient ing = RecipeHelper.buildItemIngredient(obj);
		if(ing != null) {
			ing.setMaxStackSize(count);
			return ing;
		}
		return new EmptyItemIngredient();
	}

	public IFluidIngredient getFluidIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidIngredient((Supplier<?>)obj, amount);
		}
		IFluidIngredient ing = RecipeHelper.buildFluidIngredient(obj);
		if(ing != null) {
			ing.setMaxStackSize(amount);
			return ing;
		}
		return new EmptyFluidIngredient();
	}
}
