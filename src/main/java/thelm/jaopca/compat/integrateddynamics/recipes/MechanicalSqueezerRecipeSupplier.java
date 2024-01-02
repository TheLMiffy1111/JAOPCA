package thelm.jaopca.compat.integrateddynamics.recipes;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.integrateddynamics.core.recipe.type.RecipeMechanicalSqueezer;
import org.cyclops.integrateddynamics.core.recipe.type.RecipeSqueezer;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;

public class MechanicalSqueezerRecipeSupplier implements Supplier<RecipeMechanicalSqueezer> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] itemOutput;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int time;

	public MechanicalSqueezerRecipeSupplier(ResourceLocation key, Object input, Object[] itemOutput, int time) {
		this(key, input, itemOutput, Fluids.EMPTY, 0, time);
	}

	public MechanicalSqueezerRecipeSupplier(ResourceLocation key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.time = time;
	}

	@Override
	public RecipeMechanicalSqueezer get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		NonNullList<RecipeSqueezer.ItemStackChance> itemResults = NonNullList.create();
		int i = 0;
		while(i < itemOutput.length) {
			Object out = itemOutput[i];
			++i;
			Integer count = 1;
			if(i < itemOutput.length && itemOutput[i] instanceof Integer) {
				count = (Integer)itemOutput[i];
				++i;
			}
			Float chance = 1F;
			if(i < itemOutput.length && itemOutput[i] instanceof Float) {
				chance = (Float)itemOutput[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			itemResults.add(new RecipeSqueezer.ItemStackChance(stack, chance));
		}
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(itemResults.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(itemOutput)+", "+fluidOutput);
		}
		return new RecipeMechanicalSqueezer(key, ing, itemResults, fluidStack, time);
	}
}
