package thelm.jaopca.compat.foundry.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exter.foundry.api.FoundryAPI;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputAmount;
	public final ToIntFunction<FluidStack> temperature;
	public final ToIntFunction<FluidStack> speed;

	public MeltingRecipeAction(String key, Object input, int inputCount, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> speed) {
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
		List<Object> ins = new ArrayList<>();
		if(input instanceof String && inputCount == 1) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			ins.add(input);
		}
		else {
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, inputCount, true);
			if(ing.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			ins.addAll(ing);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(Object in : ins) {
			FoundryAPI.recipes_melting.AddRecipe(in, stack, temperature.applyAsInt(stack), speed.applyAsInt(stack));
		}
		return true;
	}
}
