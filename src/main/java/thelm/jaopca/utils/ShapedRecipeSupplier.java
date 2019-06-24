package thelm.jaopca.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ShapedRecipeSupplier implements Supplier<IRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object output;
	public final int count;
	public final Object[] input;
	public ShapedRecipe recipe;

	public ShapedRecipeSupplier(ResourceLocation key, Object output, int count, Object... input) {
		this(key, "", output, count, input);
	}

	public ShapedRecipeSupplier(ResourceLocation key, String group, Object output, int count, Object... input) {
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
		int width = 0, height = 0;
		String shape = "";
		int idx = 0;
		if(input[idx] instanceof String[]) {
			String[] parts = ((String[])input[idx++]);
			for(String s : parts) {
				width = s.length();
				shape += s;
			}
			height = parts.length;
		}
		else {
			while(input[idx] instanceof String) {
				String s = (String)input[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}
		if(width * height != shape.length() || shape.length() == 0) {
			String err = "Invalid shaped recipe: ";
			for(Object tmp : input) {
				err += tmp + ", ";
			}
			err += output;
			throw new IllegalArgumentException(err);
		}
		Map<Character, Ingredient> itemMap = new HashMap<>();
		itemMap.put(' ', Ingredient.EMPTY);
		for(; idx < input.length; idx += 2)  {
			Character chr = (Character)input[idx];
			Object in = input[idx+1];
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(' ' == chr.charValue()) {
				throw new IllegalArgumentException("Invalid key entry: ' ' is a reserved symbol.");
			}
			if(ing != null)  {
				itemMap.put(chr, ing);
			}
			else {
				String err = "Invalid shaped recipe: ";
				for(Object tmp : input) {
					err += tmp + ", ";
				}
				err += output;
				throw new IllegalArgumentException(err);
			}
		}
		NonNullList<Ingredient> inputList = NonNullList.withSize(width * height, Ingredient.EMPTY);
		int x = 0;
		for(char chr : shape.toCharArray()) {
			Ingredient ing = itemMap.get(chr);
			if(ing == null) {
				throw new IllegalArgumentException("Pattern references symbol '" + chr + "' but it's not defined in the key");
			}
			inputList.set(x++, ing);
		}
		return recipe = new ShapedRecipe(key, group, width, height, inputList, stack);
	}
}
