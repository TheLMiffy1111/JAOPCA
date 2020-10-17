package thelm.jaopca.compat.crossroads.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.crafting.recipes.FluidCoolingRec;
import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;

public class FluidCoolingRecipeSupplier implements Supplier<FluidCoolingRec> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final float maxTemp;
	public final float addedHeat;

	public FluidCoolingRecipeSupplier(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		this(key, "", input, inputAmount, output, outputCount, maxTemp, addedHeat);
	}

	public FluidCoolingRecipeSupplier(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.maxTemp = maxTemp;
		this.addedHeat = addedHeat;
	}

	@Override
	public FluidCoolingRec get() {
		FluidStack ing = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new FluidCoolingRec(key, group, ing, stack, maxTemp, addedHeat, true);
	}
}
