package thelm.jaopca.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

@ZenRegister
@ZenClass("mods.jaopca.JAOPCA")
public class JAOPCA {

	@ZenMethod
	public static boolean containsModule(String moduleName) {
		return ModuleHandler.getModuleMap().containsKey(moduleName);
	}

	@ZenMethod
	public static Module getModule(String moduleName) {
		return Module.getModuleWrapper(ModuleHandler.getModuleMap().get(moduleName));
	}

	@ZenMethod
	public static boolean containsForm(String formName) {
		return FormHandler.containsForm(formName);
	}

	@ZenMethod
	public static Form getForm(String formName) {
		return Form.getFormWrapper(FormHandler.getForm(formName));
	}

	@ZenMethod
	public static boolean containsMaterial(String materialName) {
		return MaterialHandler.containsMaterial(materialName);
	}

	@ZenMethod
	public static Material getMaterial(String materialName) {
		return Material.getMaterialWrapper(MaterialHandler.getMaterial(materialName));
	}

	@ZenMethod
	public static Material[] getMaterialsForType(String materialType) {
		return MaterialHandler.getMaterials().stream().filter(m->m.getType() == MaterialType.fromName(materialType)).map(Material::getMaterialWrapper).toArray(Material[]::new);
	}

	@ZenMethod
	public static Material[] getAllMaterials() {
		return MaterialHandler.getMaterials().stream().map(Material::getMaterialWrapper).toArray(Material[]::new);
	}
}
