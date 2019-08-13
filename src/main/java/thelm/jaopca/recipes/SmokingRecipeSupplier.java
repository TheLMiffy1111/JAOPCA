package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class SmokingRecipeSupplier implements Supplier<SmokingRecipe> {

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final float experience;
	public final int time;

	public SmokingRecipeSupplier(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		this(key, "", input, output, count, experience, time);
	}

	public SmokingRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.count = count;
		this.experience = experience;
		this.time = time;
	}

	@Override
	public SmokingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new SmokingRecipe(key, group, ing, stack, experience, time);
	}
}
