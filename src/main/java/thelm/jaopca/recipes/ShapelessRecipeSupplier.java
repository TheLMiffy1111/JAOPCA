package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class ShapelessRecipeSupplier implements Supplier<ShapelessRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapelessRecipeSupplier(ResourceLocation key, Object output, int count, Object... input) {
		this(key, "", output, count, input);
	}

	public ShapelessRecipeSupplier(ResourceLocation key, String group, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public ShapelessRecipe get() {
		ItemStack stack = MiscHelper.INSTANCE.getStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		NonNullList<Ingredient> inputList = NonNullList.create();
		for(Object in : input){
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing.hasNoMatchingItems()) {
				LOGGER.warn("Empty ingredient in recipe {}: {}", key, in);
			}
			else {
				inputList.add(ing);
			}
		}
		return new ShapelessRecipe(key, group, stack, inputList);
	}
}
