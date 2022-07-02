package thelm.jaopca.compat.crafttweaker;

import com.google.common.collect.TreeBasedTable;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
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

@ZenRegister
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
		return Material.getMaterialWrapper(info.getMaterialForm().getMaterial());
	}

	@ZenMethod
	public IOreDictEntry asOreDictEntry() {
		return CraftTweakerMC.getOreDict(MiscHelper.INSTANCE.getOredictName(info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName()));
	}

	@ZenMethod
	public IItemStack asItemStack(int count) {
		if(!(info instanceof IItemProvider)) {
			return null;
		}
		return CraftTweakerMC.getIItemStack(new ItemStack(((IItemProvider)info).asItem(), count));
	}

	@ZenMethod
	public IItemStack asItemStack() {
		return asItemStack(1);
	}

	@ZenMethod
	public ILiquidStack asLiquidStack(int amount) {
		if(!(info instanceof IFluidProvider)) {
			return null;
		}
		return CraftTweakerMC.getILiquidStack(new FluidStack(((IFluidProvider)info).asFluid(), amount));
	}

	@ZenMethod
	public IBlockDefinition asBlockDefinition() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return CraftTweakerMC.getBlockDefinition(((IBlockProvider)info).asBlock());
	}

	@ZenMethod
	public IBlockState asBlockState() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return CraftTweakerMC.getBlockState((((IBlockProvider)info).asBlock().getDefaultState()));
	}
}
