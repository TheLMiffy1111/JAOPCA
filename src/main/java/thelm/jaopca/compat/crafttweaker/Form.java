package thelm.jaopca.compat.crafttweaker;

import java.util.TreeMap;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.utils.MiscHelper;

@ZenRegister
@ZenClass("mods.jaopca.Form")
public class Form {

	private static final TreeMap<IForm, Form> FORM_WRAPPERS = new TreeMap<>();
	private final IForm form;

	public static Form getFormWrapper(IForm form) {
		return FORM_WRAPPERS.computeIfAbsent(form, Form::new);
	}

	private Form(IForm form) {
		this.form = form;
	}

	public IForm getInternal() {
		return form;
	}

	@ZenGetter("name")
	public String getName() {
		return form.getName();
	}

	@ZenGetter("type")
	public String getType() {
		return form.getType().getName();
	}

	@ZenGetter("module")
	public Module getModule() {
		return Module.getModuleWrapper(form.getModule());
	}

	@ZenGetter("secondaryName")
	public String getSecondaryName() {
		return form.getSecondaryName();
	}

	@ZenGetter("materialTypes")
	public String[] getMaterialTypes() {
		return form.getMaterialTypes().stream().map(MaterialType::getName).toArray(String[]::new);
	}

	@ZenGetter("materials")
	public Material[] getMaterials() {
		return form.getMaterials().stream().map(Material::getMaterialWrapper).toArray(Material[]::new);
	}

	@ZenMethod
	public boolean containsMaterial(Material material) {
		return form.getMaterials().contains(material.getInternal());
	}

	@ZenMethod
	public IOreDictEntry getOreDictEntry(String suffix) {
		return CraftTweakerMC.getOreDict(MiscHelper.INSTANCE.getOredictName(form.getSecondaryName(), suffix));
	}

	@ZenMethod
	public IItemStack getItemStack(String suffix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		ItemStack stack = helper.getItemStack(helper.getOredictName(form.getSecondaryName(), suffix), count);
		return CraftTweakerMC.getIItemStack(stack);
	}

	@ZenMethod
	public IItemStack getItemStack(String suffix) {
		return getItemStack(suffix, 1);
	}

	@ZenMethod
	public ILiquidStack getLiquidStack(String suffix, int amount) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		FluidStack stack = helper.getFluidStack(helper.getFluidName(form.getSecondaryName(), suffix), amount);
		return CraftTweakerMC.getILiquidStack(stack);
	}

	@ZenMethod
	public MaterialForm getMaterialForm(Material material) {
		if(containsMaterial(material)) {
			return MaterialForm.getMaterialFormWrapper(form, material.getInternal());
		}
		return null;
	}

	@ZenGetter("materialForms")
	public MaterialForm[] getMaterialForms() {
		return form.getMaterials().stream().map(m->MaterialForm.getMaterialFormWrapper(form, m)).toArray(MaterialForm[]::new);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Form) {
			return form == ((Form)obj).form;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return form.hashCode()+5;
	}
}
