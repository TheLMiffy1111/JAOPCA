package thelm.jaopca.recipes;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class StonecuttingRecipeSupplier implements Supplier<StonecuttingRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;

	public StonecuttingRecipeSupplier(ResourceLocation key, Object input, Object output, int count) {
		this(key, "", input, output, count);
	}

	public StonecuttingRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int count) {
		this.key = key;
		this.group = group;
		this.input = input;
		this.output = output;
		this.count = count;
	}

	@Override
	public StonecuttingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new StonecuttingRecipe(key, group, ing, stack);
	}
}
