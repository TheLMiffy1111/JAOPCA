package thelm.jaopca.compat.hbm;

import java.util.List;
import java.util.function.Supplier;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.hbm.recipes.AnvilConstructionRecipeAction;
import thelm.jaopca.compat.hbm.recipes.CentrifugeRecipeAction;
import thelm.jaopca.compat.hbm.recipes.CrucibleSmeltingRecipeAction;
import thelm.jaopca.compat.hbm.recipes.CrystallizerRecipeAction;
import thelm.jaopca.compat.hbm.recipes.PressRecipeAction;
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
			return new RecipesCommon.ComparableStack(MiscHelper.INSTANCE.resizeItemStack((ItemStack)obj, count));
		}
		if(obj instanceof Item) {
			return new RecipesCommon.ComparableStack(new ItemStack((Item)obj, count, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof Block) {
			return new RecipesCommon.ComparableStack(new ItemStack((Block)obj, count, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof IItemProvider) {
			return new RecipesCommon.ComparableStack(new ItemStack(((IItemProvider)obj).asItem(), count, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof RecipesCommon.AStack) {
			return (RecipesCommon.AStack)obj;
		}
		return null;
	}

	public FluidStack getFluidTypeStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidTypeStack(((Supplier<?>)obj).get(), amount);
		}
		if(obj instanceof String) {
			FluidType fluidType = Fluids.fromName((String)obj);
			if(fluidType != Fluids.NONE) {
				return new FluidStack(fluidType, amount);
			}
		}
		if(obj instanceof FluidType) {
			return new FluidStack((FluidType)obj, amount);
		}
		if(obj instanceof FluidStack) {
			return new FluidStack(((FluidStack)obj).type, amount);
		}
		return null;
	}

	public Mats.MaterialStack getMaterialStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getMaterialStack(((Supplier<?>)obj).get(), amount);
		}
		if(obj instanceof String) {
			if(Mats.matByName.containsKey(obj)) {
				return new Mats.MaterialStack(Mats.matByName.get(obj), amount);
			}
		}
		if(obj instanceof NTMMaterial) {
			return new Mats.MaterialStack((NTMMaterial)obj, amount);
		}
		if(obj instanceof Mats.MaterialStack) {
			return new Mats.MaterialStack(((Mats.MaterialStack)obj).material, amount);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(obj, 1, false);
		if(stack != null) {
			List<Mats.MaterialStack> list = Mats.getMaterialsFromItem(stack);
			if(!list.isEmpty()) {
				return new Mats.MaterialStack(list.get(0).material, amount);
			}
		}
		return null;
	}

	public boolean registerCrystallizerRecipe(String key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CrystallizerRecipeAction(key, input, fluidInput, fluidInputAmount, output, outputCount, time));
	}

	public boolean registerShredderRecipe(String key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new ShredderRecipeAction(key, input, output, count));
	}

	public boolean registerCentrifugeRecipe(String key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CentrifugeRecipeAction(key, input, output));
	}

	public boolean registerCrucibleSmeltingRecipe(String key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CrucibleSmeltingRecipeAction(key, input, output));
	}

	public boolean registerAnvilConstructionRecipe(String key, Object[] input, Object[] output, int tier) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new AnvilConstructionRecipeAction(key, input, output, tier));
	}

	public boolean registerPressRecipe(String key, Object input, Object output, int count, String stampType) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new PressRecipeAction(key, input, output, count, stampType));
	}
}
