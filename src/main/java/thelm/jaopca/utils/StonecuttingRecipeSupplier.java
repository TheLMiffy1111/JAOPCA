package thelm.jaopca.utils;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.ResourceLocation;

public class StonecuttingRecipeSupplier implements Supplier<IRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public StonecuttingRecipe recipe;

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
	public IRecipe get() {
		if(recipe != null) {
			return recipe;
		}
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Invalid recipe input: "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getStack(output, count);
		if(stack == null) {
			throw new IllegalArgumentException("Invalid recipe output: "+output);
		}
		return recipe = new StonecuttingRecipe(key, group, ing, stack);
	}
}
