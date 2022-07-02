package thelm.jaopca.compat.futurepack;

import java.util.function.Supplier;

import futurepack.api.ItemPredicates;
import futurepack.depend.api.ItemStackPredicate;
import futurepack.depend.api.OreDictPredicate;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.futurepack.recipes.ZentrifugeRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class FuturepackHelper {

	public static final FuturepackHelper INSTANCE = new FuturepackHelper();

	private FuturepackHelper() {}

	public ItemPredicates getItemPredicates(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemPredicates(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemPredicates) {
			return (ItemPredicates)obj;
		}
		else if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new OreDictPredicate((String)obj, count);
			}
		}
		else if(obj instanceof ItemStack) {
			return new ItemStackPredicate(MiscHelper.INSTANCE.resizeItemStack((ItemStack)obj, count));
		}
		else if(obj instanceof Item) {
			return new ItemStackPredicate(new ItemStack((Item)obj, count, 32767));
		}
		else if(obj instanceof Block) {
			return new ItemStackPredicate(new ItemStack(Item.getItemFromBlock((Block)obj), count, 32767));
		}
		else if(obj instanceof IItemProvider) {
			return new ItemStackPredicate(new ItemStack(((IItemProvider)obj).asItem(), count, 32767));
		}
		return null;
	}

	public boolean registerZentrifugeRecipe(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ZentrifugeRecipeAction(key, input, inputCount, support, time, output));
	}
}
