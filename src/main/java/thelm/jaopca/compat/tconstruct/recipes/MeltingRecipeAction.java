package thelm.jaopca.compat.tconstruct.recipes;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputAmount;
	public final ToIntFunction<FluidStack> temperature;

	public MeltingRecipeAction(ResourceLocation key, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputAmount = outputAmount;
		this.temperature = temperature;
	}

	@Override
	public boolean register() {
		RecipeMatch ing = TConstructHelper.INSTANCE.getRecipeMatch(input, 1, outputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		TinkerRegistry.registerMelting(new MeltingRecipe(ing, stack, temperature.applyAsInt(stack)));
		return true;
	}
}
