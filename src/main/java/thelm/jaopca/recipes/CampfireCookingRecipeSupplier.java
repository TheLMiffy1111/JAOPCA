package thelm.jaopca.recipes;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class CampfireCookingRecipeSupplier implements Supplier<CampfireCookingRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final int time;

	public CampfireCookingRecipeSupplier(ResourceLocation key, Object input, Object output, int count, int time) {
		this(key, "", input, output, count, time);
	}

	public CampfireCookingRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int count, int time) {
		this.key = key;
		this.group = group;
		this.input = input;
		this.output = output;
		this.count = count;
		this.time = time;
	}

	@Override
	public CampfireCookingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new CampfireCookingRecipe(key, group, ing, stack, 0, time);
	}
}
