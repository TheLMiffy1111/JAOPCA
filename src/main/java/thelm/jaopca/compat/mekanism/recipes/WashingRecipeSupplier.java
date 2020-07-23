package thelm.jaopca.compat.mekanism.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.api.recipes.inputs.chemical.SlurryStackIngredient;
import mekanism.common.recipe.impl.FluidSlurryToSlurryIRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.mekanism.MekanismHelper;

public class WashingRecipeSupplier implements Supplier<FluidSlurryToSlurryIRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object fluidInput;
	public final int fluidInputCount;
	public final Object gasInput;
	public final int gasInputCount;
	public final Object output;
	public final int outputCount;

	public WashingRecipeSupplier(ResourceLocation key, Object fluidInput, int fluidInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.fluidInput = fluidInput;
		this.fluidInputCount = fluidInputCount;
		this.gasInput = gasInput;
		this.gasInputCount = gasInputCount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public FluidSlurryToSlurryIRecipe get() {
		SlurryStackIngredient gasIng = MekanismHelper.INSTANCE.getSlurryStackIngredient(gasInput, gasInputCount);
		FluidStackIngredient fluidIng = MekanismHelper.INSTANCE.getFluidStackIngredient(fluidInput, fluidInputCount);
		SlurryStack stack = MekanismHelper.INSTANCE.getSlurryStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new FluidSlurryToSlurryIRecipe(key, fluidIng, gasIng, stack);
	}
}
