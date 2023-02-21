package thelm.jaopca.compat.tconstruct.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class TableCastingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object cast;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int time;
	public final boolean consumeCast;

	public TableCastingRecipeAction(String key, Object cast, Object input, int inputAmount, Object output, int time, boolean consumeCast) {
		this.key = Objects.requireNonNull(key);
		this.cast = cast;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.time = time;
		this.consumeCast = consumeCast;
	}

	@Override
	public boolean register() {
		FluidStack ing = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> castIng = MiscHelper.INSTANCE.getItemStacks(cast, 1, true);
		if(castIng.isEmpty()) {
			castIng.add(null);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, 1, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack cin : castIng) {
			TConstruct.tableCasting.addCastingRecipe(stack, ing, cin, consumeCast, time);
		}
		return true;
	}
}
