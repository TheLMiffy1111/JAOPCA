package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class SmithingRecipeSupplier implements Supplier<SmithingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object base;
	public final Object addition;
	public final Object output;
	public final int count;

	public SmithingRecipeSupplier(ResourceLocation key, Object base, Object addition, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.base = base;
		this.addition = addition;
		this.output = output;
		this.count = count;
	}

	@Override
	public SmithingRecipe get() {
		Ingredient baseIng = MiscHelper.INSTANCE.getIngredient(base);
		if(baseIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+base);
		}
		Ingredient additionIng = MiscHelper.INSTANCE.getIngredient(addition);
		if(additionIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+addition);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new SmithingRecipe(key, baseIng, additionIng, stack);
	}
}
