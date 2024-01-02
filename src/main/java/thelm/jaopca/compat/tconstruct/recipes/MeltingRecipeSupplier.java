package thelm.jaopca.compat.tconstruct.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.OreMeltingRecipe;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeSupplier implements Supplier<MeltingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int outputAmount;
	public final Object[] byproducts;
	public final ToIntFunction<FluidStack> temperature;
	public final ToIntFunction<FluidStack> time;
	public final boolean isOre;

	public MeltingRecipeSupplier(ResourceLocation key, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, boolean isOre, Object... byproducts) {
		this(key, "", input, output, outputAmount, temperature, time, isOre, byproducts);
	}

	public MeltingRecipeSupplier(ResourceLocation key, String group, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, boolean isOre, Object... byproducts) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.outputAmount = outputAmount;
		this.byproducts = byproducts;
		this.temperature = temperature;
		this.time = time;
		this.isOre = isOre;
	}

	@Override
	public MeltingRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<FluidStack> bys = new ArrayList<>();
		int i = 0;
		while(i < byproducts.length) {
			Object out = byproducts[i];
			++i;
			Integer amount = 48;
			if(i < byproducts.length && byproducts[i] instanceof Integer) {
				amount = (Integer)byproducts[i];
				++i;
			}
			FluidStack by = MiscHelper.INSTANCE.getFluidStack(out, amount);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			bys.add(by);
		}
		if(isOre) {
			return new OreMeltingRecipe(key, group, ing, stack, temperature.applyAsInt(stack), time.applyAsInt(stack), bys);
		}
		else {
			return new MeltingRecipe(key, group, ing, stack, temperature.applyAsInt(stack), time.applyAsInt(stack), bys);
		}
	}
}
