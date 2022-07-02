package thelm.jaopca.compat.foundry;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import exter.foundry.api.recipe.matcher.IItemMatcher;
import exter.foundry.api.recipe.matcher.ItemStackMatcher;
import exter.foundry.api.recipe.matcher.OreMatcher;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.foundry.recipes.AtomizerRecipeAction;
import thelm.jaopca.compat.foundry.recipes.CastingRecipeAction;
import thelm.jaopca.compat.foundry.recipes.CastingTableRecipeAction;
import thelm.jaopca.compat.foundry.recipes.MeltingRecipeAction;
import thelm.jaopca.oredict.OredictHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class FoundryHelper {

	public static final FoundryHelper INSTANCE = new FoundryHelper();

	private FoundryHelper() {}

	public IItemMatcher getItemMatcher(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemMatcher(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof IItemMatcher) {
			return (IItemMatcher)obj;
		}
		else if(obj instanceof String) {
			if(OredictHandler.getOredict().contains(obj)) {
				return new OreMatcher((String)obj, count);
			}
		}
		else if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return new ItemStackMatcher(MiscHelper.INSTANCE.resizeItemStack(stack, count));
			}
		}
		else if(obj instanceof Item) {
			return new ItemStackMatcher(new ItemStack((Item)obj, count, 32767));
		}
		else if(obj instanceof Block) {
			return new ItemStackMatcher(new ItemStack(Item.getItemFromBlock((Block)obj), count, 32767));
		}
		else if(obj instanceof IItemProvider) {
			return new ItemStackMatcher(new ItemStack(((IItemProvider)obj).asItem(), count, 32767));
		}
		return null;
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> speed) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeAction(key, input, inputCount, output, outputAmount, temperature, speed));
	}

	public boolean registerCastingRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object mold, Object itemInput, int itemInputCount, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingRecipeAction(key, fluidInput, fluidInputAmount, mold, itemInput, itemInputCount, output, outputCount, speed));
	}

	public boolean registerCastingRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object mold, Object output, int outputCount, ToIntFunction<FluidStack> speed) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingRecipeAction(key, fluidInput, fluidInputAmount, mold, output, outputCount, speed));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, String type) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeAction(key, input, inputAmount, output, outputCount, type));
	}

	public boolean registerAtomizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AtomizerRecipeAction(key, input, inputAmount, output, outputCount));
	}
}
