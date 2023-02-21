package thelm.jaopca.compat.hbm.recipes;

import java.util.Locale;
import java.util.Objects;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.PressRecipes;
import com.hbm.items.machine.ItemStamp;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.hbm.HBMHelper;
import thelm.jaopca.utils.MiscHelper;

public class PressRecipeAction implements IRecipeAction {

	public final String key;
	public final Object input;
	public final Object output;
	public final int count;
	public final ItemStamp.StampType stampType;

	public PressRecipeAction(String key, Object input, Object output, int count, String stampType) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.stampType = ItemStamp.StampType.valueOf(stampType.toUpperCase(Locale.US));
	}

	@Override
	public boolean register() {
		RecipesCommon.AStack ing = HBMHelper.INSTANCE.getAStack(input, 1);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		PressRecipes.makeRecipe(stampType, ing, stack);
		return true;
	}
}
