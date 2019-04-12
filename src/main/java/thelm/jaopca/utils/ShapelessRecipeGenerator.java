package thelm.jaopca.utils;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ShapelessRecipeGenerator implements Supplier<IRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object output;
	public final int count;
	public final Object[] input;
	public ShapelessRecipe recipe;

	public ShapelessRecipeGenerator(ResourceLocation key, Object output, int count, Object... input) {
		this(key, "", output, count, input);
	}

	public ShapelessRecipeGenerator(ResourceLocation key, String group, Object output, int count, Object... input) {
		this.key = key;
		this.group = group;
		this.output = output;
		this.count = count;
		this.input = input;
	}

	@Override
	public IRecipe get() {
		if(recipe != null) {
			return recipe;
		}
		ItemStack stack = MiscHelper.INSTANCE.getStack(output, count);
		if(stack == null) {
			throw new IllegalArgumentException("Invalid recipe output: "+output);
		}
		NonNullList<Ingredient> inputList = NonNullList.create();
		for(Object in : input){
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing != null) {
				inputList.add(ing);
			}
			else {
				String err = "Invalid shapeless recipe: ";
				for(Object tmp : input) {
					err += tmp + ", ";
				}
				err += output;
				throw new IllegalArgumentException(err);
			}
		}
		return recipe = new ShapelessRecipe(key, group, stack, inputList);
	}
}
