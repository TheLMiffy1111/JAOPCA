package thelm.jaopca.compat.crossroads.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.crafting.recipes.BlastFurnaceRec;
import com.google.common.base.Strings;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;

public class BlastFurnaceRecipeSupplier implements Supplier<BlastFurnaceRec> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int amount;
	public final int slagCount;

	public BlastFurnaceRecipeSupplier(ResourceLocation key, Object input, Object output, int amount, int slagCount) {
		this(key, "", input, output, amount, slagCount);
	}

	public BlastFurnaceRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int amount, int slagCount) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.amount = amount;
		this.slagCount = slagCount;
	}

	@Override
	public BlastFurnaceRec get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, amount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new BlastFurnaceRec(key, group, ing, stack, slagCount, true);
	}
}
