package thelm.jaopca.compat.groovyscript;

import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.blocks.IBlockProvider;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

public class MaterialFormInfoExpansion {

	public static IForm getForm(IMaterialFormInfo info) {
		return info.getMaterialForm().getForm();
	}

	public static IMaterial getMaterial(IMaterialFormInfo info) {
		return info.getMaterialForm().getMaterial();
	}

	public static OreDictIngredient asOre(IMaterialFormInfo info) {
		return new OreDictIngredient(MiscHelper.INSTANCE.getOredictName(info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName()));
	}

	public static ItemStack asItem(IMaterialFormInfo info, int count) {
		if(info instanceof IItemProvider) {
			return new ItemStack(((IItemProvider)info).asItem(), count);
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack asItem(IMaterialFormInfo info) {
		return asItem(info, 1);
	}

	public static FluidStack asFluid(IMaterialFormInfo info, int amount) {
		if(info instanceof IFluidProvider) {
			return new FluidStack(((IFluidProvider)info).asFluid(), amount);
		}
		return null;
	}

	public static FluidStack asLiquid(IMaterialFormInfo info, int amount) {
		return asFluid(info, amount);
	}

	public static Block asBlock(IMaterialFormInfo info) {
		if(info instanceof IBlockProvider) {
			return ((IBlockProvider)info).asBlock();
		}
		return null;
	}

	public static IBlockState asBlockState(IMaterialFormInfo info) {
		if(info instanceof IBlockProvider) {
			return ((IBlockProvider)info).asBlock().getDefaultState();
		}
		return null;
	}
}
