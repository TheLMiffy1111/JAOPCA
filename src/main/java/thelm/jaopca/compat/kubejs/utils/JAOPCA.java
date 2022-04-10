package thelm.jaopca.compat.kubejs.utils;

import java.util.List;
import java.util.stream.Collectors;

import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class JAOPCA {

	public static boolean containsModule(String moduleName) {
		return ModuleHandler.getModuleMap().containsKey(moduleName);
	}

	public static Module getModule(String moduleName) {
		return Module.getModuleWrapper(ModuleHandler.getModuleMap().get(moduleName));
	}

	public static boolean containsForm(String formName) {
		return FormHandler.containsForm(formName);
	}

	public static Form getForm(String formName) {
		return Form.getFormWrapper(FormHandler.getForm(formName));
	}

	public static boolean containsMaterial(String materialName) {
		return MaterialHandler.containsMaterial(materialName);
	}

	public static Material getMaterial(String materialName) {
		return Material.getMaterialWrapper(MaterialHandler.getMaterial(materialName));
	}

	public static List<Material> getMaterialsForType(String materialType) {
		return MaterialHandler.getMaterials().stream().filter(m->m.getType() == MaterialType.fromName(materialType)).map(Material::getMaterialWrapper).collect(Collectors.toList());
	}

	public static List<Material> getAllMaterials() {
		return MaterialHandler.getMaterials().stream().map(Material::getMaterialWrapper).collect(Collectors.toList());
	}
}
