package thelm.jaopca.compat.kubejs.utils;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.modules.ModuleHandler;

public class Module {

	private static final TreeMap<IModule, Module> MODULE_WRAPPERS = new TreeMap<>();
	private final IModule module;
	private final IModuleData moduleData;

	public static Module getModuleWrapper(IModule form) {
		return MODULE_WRAPPERS.computeIfAbsent(form, Module::new);
	}

	private Module(IModule module) {
		this.module = module;
		this.moduleData = ModuleHandler.getModuleData(module);
	}

	public IModule getInternal() {
		return module;
	}

	public String getName() {
		return module.getName();
	}

	public List<String> getMaterialTypes() {
		return module.getMaterialTypes().stream().map(MaterialType::getName).collect(Collectors.toList());
	}

	public List<Material> getMaterials() {
		return moduleData.getMaterials().stream().map(Material::getMaterialWrapper).collect(Collectors.toList());
	}

	public boolean containsMaterial(Material material) {
		return moduleData.getMaterials().contains(material.getInternal());
	}

	public List<Form> getForms() {
		return FormHandler.getForms().stream().filter(f->f.getModule() == module).map(Form::getFormWrapper).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Module) {
			return module == ((Module)obj).module;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return module.hashCode()+5;
	}
}
