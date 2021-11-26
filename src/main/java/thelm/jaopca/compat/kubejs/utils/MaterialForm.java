package thelm.jaopca.compat.kubejs.utils;

import com.google.common.collect.TreeBasedTable;

import dev.latvian.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import thelm.jaopca.api.blocks.IBlockProvider;
import thelm.jaopca.api.fluids.IFluidProvider;
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

	public TagIngredientJS asTag() {
		return TagIngredientJS.createTag(MiscHelper.INSTANCE.getTagLocation(
				info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName(),
				info.getMaterialForm().getForm().getTagSeparator()).
				toString());
	}

	public ItemStackJS asItemStack(int count) {
		return ItemStackJS.of(info);
	}

	public ItemStackJS asItemStack() {
		return asItemStack(1);
	}

	public FluidStackJS asFluidStack(int amount) {
		if(!(info instanceof IFluidProvider)) {
			return EmptyFluidStackJS.INSTANCE;
		}
		return FluidStackJS.of(((IFluidProvider)info).asFluid(), amount, null);
	}

	public Block asBlock() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return ((IBlockProvider)info).asBlock();
	}

	public BlockState asBlockState() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return (((IBlockProvider)info).asBlock().getDefaultState());
	}
}
