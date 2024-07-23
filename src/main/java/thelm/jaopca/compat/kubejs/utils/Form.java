package thelm.jaopca.compat.kubejs.utils;

import java.util.List;
import java.util.TreeMap;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.utils.MiscHelper;

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

	public String getName() {
		return form.getName();
	}

	public String getType() {
		return form.getType().getName();
	}

	public Module getModule() {
		return Module.getModuleWrapper(form.getModule());
	}

	public String getSecondaryName() {
		return form.getSecondaryName();
	}

	public List<String> getMaterialTypes() {
		return form.getMaterialTypes().stream().map(MaterialType::getName).toList();
	}

	public List<Material> getMaterials() {
		return form.getMaterials().stream().map(Material::getMaterialWrapper).toList();
	}

	public String getTagSeparator() {
		return form.getTagSeparator();
	}

	public boolean containsMaterial(Material material) {
		return form.getMaterials().contains(material.getInternal());
	}

	public String getTag(String suffix) {
		return MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), suffix, form.getTagSeparator()).toString();
	}

	public ItemStack getItemStack(String suffix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		return helper.getItemStack(helper.getTagLocation(form.getSecondaryName(), suffix), count);
	}

	public ItemStack getItemStack(String suffix) {
		return getItemStack(suffix, 1);
	}

	public FluidStackJS getFluidStack(String suffix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		FluidStack stack = helper.getFluidStack(helper.getTagLocation(form.getSecondaryName(), suffix), count);
		return FluidStackJS.of(FluidStackHooksForge.fromForge(stack));
	}

	public MaterialForm getMaterialForm(Material material) {
		if(!containsMaterial(material)) {
			return null;
		}
		return MaterialForm.getMaterialFormWrapper(form, material.getInternal());
	}

	public List<MaterialForm> getMaterialForms() {
		return form.getMaterials().stream().map(m->MaterialForm.getMaterialFormWrapper(form, m)).toList();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Form other)) {
			return false;
		}
		return form == other.form;
	}

	@Override
	public int hashCode() {
		return form.hashCode()+5;
	}
}
