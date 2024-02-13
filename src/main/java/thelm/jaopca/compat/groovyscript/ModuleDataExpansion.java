package thelm.jaopca.compat.groovyscript;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.forms.FormHandler;

public class ModuleDataExpansion {

	public static String getName(IModuleData data) {
		return data.getModule().getName();
	}

	public static Set<MaterialType> getMaterialTypes(IModuleData data) {
		return data.getModule().getMaterialTypes();
	}

	public static boolean containsMaterial(IModuleData data, IMaterial material) {
		return data.getMaterials().contains(material);
	}

	public static List<IForm> getForms(IModuleData data) {
		return FormHandler.getForms().stream().filter(f->f.getModule() == data.getModule()).collect(Collectors.toList());
	}
}
