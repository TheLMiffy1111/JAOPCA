package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import electrodynamics.common.recipe.categories.o2o.specificmachines.MineralCrusherRecipe;
import electrodynamics.common.recipe.recipeutils.CountableIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;

public class MineralCrusherRecipeSupplier implements Supplier<MineralCrusherRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;

	public MineralCrusherRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public MineralCrusherRecipe get() {
		CountableIngredient ing = ElectrodynamicsHelper.INSTANCE.getCountableIngredient(input, inputCount);
		if(ing.fetchCountedStacks().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new MineralCrusherRecipe(key, ing, stack);
	}
}
