package thelm.jaopca.compat.crossroads.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.crafting.recipes.CrucibleRec;
import com.google.common.base.Strings;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;

public class CrucibleRecipeSupplier implements Supplier<CrucibleRec> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int amount;

	public CrucibleRecipeSupplier(ResourceLocation key, Object input, Object output, int amount) {
		this(key, "", input, output, amount);
	}

	public CrucibleRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int amount) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.amount = amount;
	}

	@Override
	public CrucibleRec get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, amount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		return new CrucibleRec(key, group, ing, stack, true);
	}
}
