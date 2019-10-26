package thelm.jaopca.compat.crafttweaker;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;

import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.materials.MaterialHandler;

@ZenRegister
@ZenCodeType.Name("mods.jaopca.JAOPCA")
public class JAOPCA {

	@ZenCodeType.Method
	public static boolean containsForm(String formName) {
		return FormHandler.containsForm(formName);
	}

	@ZenCodeType.Method
	public static Form getForm(String formName) {
		return Form.getFormWrapper(FormHandler.getForm(formName));
	}

	@ZenCodeType.Method
	public static boolean containsMaterial(String materialName) {
		return MaterialHandler.containsMaterial(materialName);
	}

	@ZenCodeType.Method
	public static Material getMaterial(String materialName) {
		return Material.getMaterialWrapper(MaterialHandler.getMaterial(materialName));
	}

	@ZenCodeType.Method
	public static Material[] getMaterialsForType(String materialType) {
		return MaterialHandler.getMaterials().stream().filter(m->m.getType() == MaterialType.fromName(materialType)).map(Material::getMaterialWrapper).toArray(Material[]::new);
	}

	@ZenCodeType.Method
	public static Material[] getAllMaterials() {
		return MaterialHandler.getMaterials().stream().map(Material::getMaterialWrapper).toArray(Material[]::new);
	}
}
