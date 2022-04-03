package thelm.jaopca.compat.kubejs.utils;

import com.google.common.collect.TreeBasedTable;

import dev.latvian.mods.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

	public ItemStackJS asItemStack(int count) {
		return ItemStackJS.of(info);
	}

	public ItemStackJS asItemStack() {
		return asItemStack(1);
	}

	public FluidStackJS asFluidStack(int amount) {
		if(!(info instanceof IFluidLike)) {
			return EmptyFluidStackJS.INSTANCE;
		}
		return FluidStackJS.of(((IFluidLike)info).asFluid(), amount, null);
	}

	public Block asBlock() {
		if(!(info instanceof IBlockLike)) {
			return null;
		}
		return ((IBlockLike)info).asBlock();
	}

	public BlockState asBlockState() {
		if(!(info instanceof IBlockLike)) {
			return null;
		}
		return (((IBlockLike)info).asBlock().defaultBlockState());
	}
}
