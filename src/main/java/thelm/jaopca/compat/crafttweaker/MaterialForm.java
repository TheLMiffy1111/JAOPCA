package thelm.jaopca.compat.crafttweaker;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.blocks.MCBlock;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.google.common.collect.TreeBasedTable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.blocks.IBlockProvider;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

@ZenRegister
@ZenCodeType.Name("mods.jaopca.MaterialForm")
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

	@ZenCodeType.Getter("form")
	public Form getForm() {
		return Form.getFormWrapper(info.getMaterialForm().getForm());
	}

	@ZenCodeType.Getter("material")
	public Material getMaterial() {
		return Material.getMaterialWrapper(info.getMaterialForm().getMaterial());
	}

	@ZenCodeType.Method
	public MCTag asTag() {
		return new MCTag(MiscHelper.INSTANCE.getTagLocation(
				info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName()));
	}

	@ZenCodeType.Method
	public IItemStack asItemStack(int count) {
		if(!(info instanceof IItemProvider)) {
			return null;
		}
		return new MCItemStack(new ItemStack((IItemProvider)info, count));
	}

	@ZenCodeType.Method
	public IItemStack asItemStack() {
		return asItemStack(1);
	}

	@ZenCodeType.Method
	public MCBlock asBlock() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return new MCBlock(((IBlockProvider)info).asBlock());
	}

	@ZenCodeType.Method
	public MCBlockState asBlockState() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return new MCBlockState(((IBlockProvider)info).asBlock().getDefaultState());
	}
}
