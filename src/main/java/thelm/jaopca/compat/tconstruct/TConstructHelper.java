package thelm.jaopca.compat.tconstruct;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.tconstruct.recipes.BasinCastingRecipeAction;
import thelm.jaopca.compat.tconstruct.recipes.MeltingRecipeAction;
import thelm.jaopca.compat.tconstruct.recipes.TableCastingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class TConstructHelper {

	public static final TConstructHelper INSTANCE = new TConstructHelper();

	private TConstructHelper() {}

	public RecipeMatch getRecipeMatch(Object obj, int count, int matched) {
		if(obj instanceof Supplier<?>) {
			return getRecipeMatch(((Supplier<?>)obj).get(), count, matched);
		}
		else if(obj instanceof RecipeMatch) {
			return (RecipeMatch)obj;
		}
		else if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return RecipeMatch.of((String)obj, count, matched);
			}
		}
		else if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return RecipeMatch.of((ItemStack)obj, count, matched);
			}
		}
		else if(obj instanceof Item) {
			if(obj != Items.AIR) {
				return RecipeMatch.of((Item)obj, count, matched);
			}
		}
		else if(obj instanceof Block) {
			if(obj != Blocks.AIR) {
				return RecipeMatch.of(Item.getItemFromBlock((Block)obj), count, matched);
			}
		}
		else if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return RecipeMatch.of(item, count, matched);
			}
		}
		return null;
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeAction(key, input, output, outputAmount, temperature));
	}

	public boolean registerTableCastingRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new TableCastingRecipeAction(key, cast, input, inputAmount, output, time, consumeCast, switchSlots));
	}

	public boolean registerBasinCastingRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BasinCastingRecipeAction(key, cast, input, inputAmount, output, time, consumeCast, switchSlots));
	}
}
