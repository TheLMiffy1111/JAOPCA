package thelm.jaopca.compat.kubejs.utils;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.forms.IForm;
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
		return form.getMaterialTypes().stream().map(MaterialType::getName).collect(Collectors.toList());
	}

	public List<Material> getMaterials() {
		return form.getMaterials().stream().map(Material::getMaterialWrapper).collect(Collectors.toList());
	}

	public String getTagSeparator() {
		return form.getTagSeparator();
	}

	public boolean containsMaterial(Material material) {
		return form.getMaterials().contains(material.getInternal());
	}

	public TagIngredientJS getTag(String suffix) {
		return TagIngredientJS.createTag(MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), suffix, form.getTagSeparator()).toString());
	}

	public ItemStackJS getItemStack(String suffix, int count) {
		MiscHelper helper = MiscHelper.INSTANCE;
		ItemStack stack = helper.getItemStack(helper.getTagLocation(form.getSecondaryName(), suffix), count);
		return ItemStackJS.of(stack);
	}

	public ItemStackJS getItemStack(String suffix) {
		return getItemStack(suffix, 1);
	}

	public MaterialForm getMaterialForm(Material material) {
		if(!containsMaterial(material)) {
			return null;
		}
		return MaterialForm.getMaterialFormWrapper(form, material.getInternal());
	}

	public List<MaterialForm> getMaterialForms() {
		return form.getMaterials().stream().map(m->MaterialForm.getMaterialFormWrapper(form, m)).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Form)) {
			return false;
		}
		Form other = (Form)obj;
		return form == other.form;
	}

	@Override
	public int hashCode() {
		return form.hashCode()+5;
	}
}
