package thelm.jaopca.compat.kubejs.utils;

import com.google.common.collect.TreeBasedTable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.blocks.IBlockLike;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

public class MaterialForm {

	private static final TreeBasedTable<IForm, IMaterial, MaterialForm> MATERIAL_FORM_WRAPPERS = TreeBasedTable.create();
	private final IMaterialFormInfo info;

	public static MaterialForm getMaterialFormWrapper(IForm form, IMaterial material) {
		MaterialForm materialForm = MATERIAL_FORM_WRAPPERS.get(form, material);
		if(materialForm == null) {
			IMaterialFormInfo info = form.getType().getMaterialFormInfo(form, material);
			materialForm = new MaterialForm(info);
		}
		return materialForm;
	}

	private MaterialForm(IMaterialFormInfo info) {
		this.info = info;
	}

	public IMaterialFormInfo getInternal() {
		return info;
	}

	public Form getForm() {
		return Form.getFormWrapper(info.getMaterialForm().getForm());
	}

	public Material getMaterial() {
		return Material.getMaterialWrapper(info.getMaterialForm().getMaterial());
	}

	public String asTag() {
		return MiscHelper.INSTANCE.getTagLocation(
				info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName(),
				info.getMaterialForm().getForm().getTagSeparator()).
				toString();
	}

	public ItemStack asItemStack(int count) {
		if(info instanceof ItemLike item) {
			return new ItemStack(item, count);
		}
		return ItemStack.EMPTY;
	}

	public ItemStack asItemStack() {
		return asItemStack(1);
	}

	public FluidStack asFluidStack(int amount) {
		if(info instanceof IFluidLike fluid) {
			return new FluidStack(fluid.asFluid(), amount);
		}
		return FluidStack.EMPTY;
	}

	public Block asBlock() {
		if(info instanceof IBlockLike block) {
			return block.asBlock();
		}
		return null;
	}

	public BlockState asBlockState() {
		if(info instanceof IBlockLike block) {
			return block.asBlock().defaultBlockState();
		}
		return null;
	}
}
