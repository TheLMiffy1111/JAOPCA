package thelm.jaopca.utils;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.util.ResourceLocation;

public class SmokingRecipeSupplier implements Supplier<IRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final float experience;
	public final int time;
	public SmokingRecipe recipe;

	public SmokingRecipeSupplier(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		this(key, "", input, output, count, experience, time);
	}

	public SmokingRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		this.key = key;
		this.group = group;
		this.input = input;
		this.output = output;
		this.count = count;
		this.experience = experience;
		this.time = time;
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
		return recipe = new SmokingRecipe(key, group, ing, stack, experience, time);
	}
}
