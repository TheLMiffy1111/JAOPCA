package thelm.jaopca.compat.foundry.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exter.foundry.api.FoundryAPI;
import exter.foundry.api.orestack.OreStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CastingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object mold;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object output;
	public final int outputCount;
	public final ToIntFunction<FluidStack> speed;

	public CastingRecipeAction(String key, Object fluidInput, int fluidInputAmount, Object mold, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		this(key, fluidInput, fluidInputAmount, mold, null, 0, output, outputCount, speed);
	}

	public CastingRecipeAction(String key, Object fluidInput, int fluidInputAmount, Object mold, Object itemInput, int itemInputCount, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		this.key = Objects.requireNonNull(key);
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.mold = mold;
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.speed = speed;
	}

	@Override
	public boolean register() {
		FluidStack fluidIng = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		List<ItemStack> moldIng = MiscHelper.INSTANCE.getItemStacks(mold, 1, true);
		if(moldIng.isEmpty()) {
			throw new IllegalArgumentException("Empty mold in recipe "+key+": "+mold);
		}
		List<Object> itemIns = new ArrayList<>();
		if(itemInput instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(itemInput)) {
				LOGGER.warn("Empty input in recipe {}: {}", new Object[] {key, itemInput});
			}
			itemIns.add(new OreStack((String)itemInput, itemInputCount));
		}
		else {
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(itemInput, itemInputCount, true);
			if(ing.isEmpty()) {
				ing.add(null);
			}
			itemIns.addAll(ing);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack moldIn : moldIng) {
			for(Object itemIn : itemIns) {
				FoundryAPI.recipes_casting.AddRecipe(stack, fluidIng, moldIn.copy(), itemIn, speed.applyAsInt(fluidIng));
			}
		}
		return true;
	}
}
