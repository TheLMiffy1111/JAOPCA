package thelm.jaopca.compat.hbm;

import java.util.function.Supplier;

import com.hbm.inventory.RecipesCommon;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.hbm.recipes.AnvilConstructionRecipeAction;
import thelm.jaopca.compat.hbm.recipes.CentrifugeRecipeAction;
import thelm.jaopca.compat.hbm.recipes.CrystallizerRecipeAction;
import thelm.jaopca.compat.hbm.recipes.ShredderRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class HBMHelper {

	public static final HBMHelper INSTANCE = new HBMHelper();

	private HBMHelper() {}

	public RecipesCommon.AStack getAStack(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getAStack(((Supplier<?>)obj).get(), count);
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new RecipesCommon.OreDictStack((String)obj, count);
			}
		}
		if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return new RecipesCommon.ComparableStack(MiscHelper.INSTANCE.resizeItemStack(stack, count));
			}
		}
		if(obj instanceof Item) {
			if(obj != Items.AIR) {
				return new RecipesCommon.ComparableStack(new ItemStack((Item)obj, count));
			}
		}
		if(obj instanceof Block) {
			if(obj != Blocks.AIR) {
				return new RecipesCommon.ComparableStack(new ItemStack((Block)obj, count));
			}
		}
		if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return new RecipesCommon.ComparableStack(new ItemStack(((IItemProvider)obj).asItem(), count));
			}
		}
		return null;
	}

	public boolean registerCrystallizerRecipe(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CrystallizerRecipeAction(key, input, fluidInput, fluidInputAmount, output, count));
	}

	public boolean registerShredderRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new ShredderRecipeAction(key, input, output, count));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CentrifugeRecipeAction(key, input, output));
	}

	public boolean registerAnvilConstructionRecipe(ResourceLocation key, Object[] input, Object[] output, int tier) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new AnvilConstructionRecipeAction(key, input, output, tier));
	}
}
