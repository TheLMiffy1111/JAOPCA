package thelm.jaopca.compat.foundry.recipes;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exter.foundry.api.FoundryAPI;
import exter.foundry.api.recipe.matcher.IItemMatcher;
import exter.foundry.api.recipe.matcher.ItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.foundry.FoundryHelper;
import thelm.jaopca.utils.MiscHelper;

public class CastingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object mold;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object output;
	public final int outputCount;
	public final ToIntFunction<FluidStack> speed;

	public CastingRecipeAction(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object mold, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		this(key, fluidInput, fluidInputAmount, mold, ItemStack.EMPTY, 0, output, outputCount, speed);
	}

	public CastingRecipeAction(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object mold, Object itemInput, int itemInputCount, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
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
		Ingredient moldIng = MiscHelper.INSTANCE.getIngredient(mold);
		if(moldIng == null) {
			throw new IllegalArgumentException("Empty mold in recipe "+key+": "+mold);
		}
		IItemMatcher itemIng = FoundryHelper.INSTANCE.getItemMatcher(itemInput, itemInputCount);
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack moldIn : moldIng.getMatchingStacks()) {
			FoundryAPI.CASTING_MANAGER.addRecipe(new ItemStackMatcher(stack), fluidIng, moldIn.copy(), itemIng, speed.applyAsInt(fluidIng));
		}
		return true;
	}
}
