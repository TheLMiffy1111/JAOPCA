package thelm.jaopca.compat.minetweaker;

import com.google.common.collect.TreeBasedTable;

import minetweaker.api.block.IBlockDefinition;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.mc1710.liquid.MCLiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.blocks.IBlockProvider;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

@ZenClass("mods.jaopca.MaterialForm")
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

	@ZenGetter("form")
	public Form getForm() {
		return Form.getFormWrapper(info.getMaterialForm().getForm());
	}

	@ZenGetter("material")
	public Material getMaterial() {
		return Material.getMaterialWrapper(info.getMaterialForm().getIMaterial());
	}

	@ZenMethod
	public IOreDictEntry asOreDictEntry() {
		return MineTweakerMC.getOreDict(MiscHelper.INSTANCE.getOredictName(info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getIMaterial().getName()));
	}

	@ZenMethod
	public IItemStack asItemStack(int count) {
		if(info instanceof IItemProvider) {
			return MineTweakerMC.getIItemStack(new ItemStack(((IItemProvider)info).asItem(), count));
		}
		return null;
	}

	@ZenMethod
	public IItemStack asItemStack() {
		return asItemStack(1);
	}

	@ZenMethod
	public ILiquidStack asLiquidStack(int amount) {
		if(info instanceof IFluidProvider) {
			return new MCLiquidStack(new FluidStack(((IFluidProvider)info).asFluid(), amount));
		}
		return null;
	}

	@ZenMethod
	public IBlockDefinition asBlockDefinition() {
		if(info instanceof IBlockProvider) {
			return MineTweakerMC.getBlockDefinition(((IBlockProvider)info).asBlock());
		}
		return null;
	}
}
