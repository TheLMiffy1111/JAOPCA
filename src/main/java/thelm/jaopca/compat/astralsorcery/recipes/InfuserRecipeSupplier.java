package thelm.jaopca.compat.astralsorcery.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;

public class InfuserRecipeSupplier implements Supplier<LiquidInfusion> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object liquidInput;
	public final Object itemInput;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final float consumeChance;
	public final boolean consumeMultiple;
	public final boolean acceptChalice;
	public final boolean copyNBT;

	public InfuserRecipeSupplier(ResourceLocation key, Object liquidInput, Object itemInput, Object output, int outputCount, int time, float consumeChance, boolean consumeMultiple, boolean acceptChalice, boolean copyNBT) {
		this.key = Objects.requireNonNull(key);
		this.liquidInput = liquidInput;
		this.itemInput = itemInput;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.consumeChance = consumeChance;
		this.consumeMultiple = consumeMultiple;
		this.acceptChalice = acceptChalice;
		this.copyNBT = copyNBT;
	}

	@Override
	public LiquidInfusion get() {
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(liquidInput, 1);
		if(fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty fluid in recipe "+key+": "+liquidInput);
		}
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new LiquidInfusion(key, time, fluidStack.getFluid(), ing, stack, consumeChance, consumeMultiple, acceptChalice, copyNBT);
	}
}
