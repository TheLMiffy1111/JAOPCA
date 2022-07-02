package thelm.jaopca.compat.foundry.recipes;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exter.foundry.api.FoundryAPI;
import exter.foundry.api.recipe.matcher.IItemMatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.foundry.FoundryHelper;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputAmount;
	public final ToIntFunction<FluidStack> temperature;
	public final ToIntFunction<FluidStack> speed;

	public MeltingRecipeAction(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> speed) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.temperature = temperature;
		this.speed = speed;
	}

	@Override
	public boolean register() {
		IItemMatcher ing = FoundryHelper.INSTANCE.getItemMatcher(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		FoundryAPI.MELTING_MANAGER.addRecipe(ing, stack, temperature.applyAsInt(stack), speed.applyAsInt(stack));
		return true;
	}
}
