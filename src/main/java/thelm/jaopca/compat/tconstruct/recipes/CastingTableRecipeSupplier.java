package thelm.jaopca.compat.tconstruct.recipes;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.utils.MiscHelper;

public class CastingTableRecipeSupplier implements Supplier<ItemCastingRecipe.Table> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object cast;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final ToIntFunction<FluidStack> time;
	public final boolean consumeCast;
	public final boolean switchSlots;

	public CastingTableRecipeSupplier(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		this(key, "", cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots);
	}

	public CastingTableRecipeSupplier(ResourceLocation key, String group, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.cast = cast;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.consumeCast = consumeCast;
		this.switchSlots = switchSlots;
	}

	@Override
	public ItemCastingRecipe.Table get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(cast);
		FluidIngredient fluidIng = TConstructHelper.INSTANCE.getFluidIngredient(input, inputAmount);
		if(fluidIng.getFluids().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack funcStack = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		ItemOutput out = TConstructHelper.INSTANCE.getItemOutput(output, outputCount);
		if(out.get().isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new ItemCastingRecipe.Table(key, group, ing, fluidIng, out, time.applyAsInt(funcStack), consumeCast, switchSlots);
	}
}
