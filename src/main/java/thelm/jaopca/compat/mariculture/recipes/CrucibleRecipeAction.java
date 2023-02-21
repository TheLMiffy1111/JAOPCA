package thelm.jaopca.compat.mariculture.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mariculture.core.helpers.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CrucibleRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final Object itemOutput;
	public final int itemOutputCount;
	public final int itemOutputChance;
	public final int temperature;

	public CrucibleRecipeAction(String key, Object input, int inputCount, Object fluidOutput, int fluidOutputAmount, int temperature) {
		this(key, input, inputCount, fluidOutput, fluidOutputAmount, null, 0, 0, temperature);
	}

	public CrucibleRecipeAction(String key, Object input, int inputCount, Object fluidOutput, int fluidOutputAmount, Object itemOutput, int itemOutputCount, int itemOutputChance, int temperature) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.itemOutput = itemOutput;
		this.itemOutputCount = itemOutputCount;
		this.itemOutputChance = itemOutputChance;
		this.temperature = temperature;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+fluidOutput);
		}
		ItemStack extra = MiscHelper.INSTANCE.getItemStack(itemOutput, itemOutputCount, false);
		for(ItemStack in : ing) {
			RecipeHelper.addMelting(in, temperature, stack, extra, itemOutputChance);
		}
		return true;
	}
}
