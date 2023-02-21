package thelm.jaopca.compat.randomadditions;

import java.util.function.Supplier;

import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.creativecore.common.utils.stack.StackInfoBlock;
import com.creativemd.creativecore.common.utils.stack.StackInfoItem;
import com.creativemd.creativecore.common.utils.stack.StackInfoItemStack;
import com.creativemd.creativecore.common.utils.stack.StackInfoOre;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.randomadditions.recipes.CrusherRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class RandomAdditionsHelper {

	public static final RandomAdditionsHelper INSTANCE = new RandomAdditionsHelper();

	private RandomAdditionsHelper() {}

	public StackInfo getStackInfo(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getStackInfo(((Supplier<?>)obj).get(), count);
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new StackInfoOre((String)obj, count);
			}
		}
		if(obj instanceof ItemStack) {
			return new StackInfoItemStack(MiscHelper.INSTANCE.resizeItemStack((ItemStack)obj, count));
		}
		if(obj instanceof Item) {
			return new StackInfoItem((Item)obj, count);
		}
		if(obj instanceof Block) {
			return new StackInfoBlock((Block)obj, count);
		}
		else if(obj instanceof IItemProvider) {
			return new StackInfoItem(((IItemProvider)obj).asItem(), count);
		}
		if(obj instanceof StackInfo) {
			return (StackInfo)obj;
		}
		return null;
	}

	public boolean registerCrusherRecipe(String key, Object input, int inputCount, Object output, int outputCount, int power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, inputCount, output, outputCount, power));
	}
}
