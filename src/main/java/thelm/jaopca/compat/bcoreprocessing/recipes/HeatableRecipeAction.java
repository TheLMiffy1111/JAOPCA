package thelm.jaopca.compat.bcoreprocessing.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class HeatableRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputAmount;
	public final int heatFrom;
	public final int heatTo;

	public HeatableRecipeAction(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, int heatFrom, int heatTo) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.heatFrom = heatFrom;
		this.heatTo = heatTo;
	}

	@Override
	public boolean register() {
		FluidStack ing = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BuildcraftRecipeRegistry.refineryRecipes.addHeatableRecipe(ing, stack, heatFrom, heatTo);
		return true;
	}
}
