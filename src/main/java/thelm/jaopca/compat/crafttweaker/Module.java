package thelm.jaopca.compat.crafttweaker;

import java.util.TreeMap;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.modules.ModuleHandler;

@ZenRegister
@ZenClass("mods.jaopca.Module")
public class Module {

	private static final TreeMap<IModule, Module> MODULE_WRAPPERS = new TreeMap<>();
	private final IModule module;
	private final IModuleData moduleData;

	public static Module getModuleWrapper(IModule module) {
		return MODULE_WRAPPERS.computeIfAbsent(module, Module::new);
	}

	private Module(IModule module) {
		this.module = module;
		moduleData = ModuleHandler.getModuleData(module);
	}

	public IModule getInternal() {
		return module;
	}

	@ZenGetter("name")
	public String getName() {
		return module.getName();
	}

	@ZenGetter("materialTypes")
	public String[] getMaterialTypes() {
		return module.getMaterialTypes().stream().map(MaterialType::getName).toArray(String[]::new);
	}

	@ZenGetter("materials")
	public Material[] getMaterials() {
		return moduleData.getMaterials().stream().map(Material::getMaterialWrapper).toArray(Material[]::new);
	}

	@ZenMethod
	public boolean containsMaterial(Material material) {
		return moduleData.getMaterials().contains(material.getInternal());
	}

	@ZenGetter("forms")
	public Form[] getForms() {
		return FormHandler.getForms().stream().filter(f->f.getModule() == module).map(Form::getFormWrapper).toArray(Form[]::new);
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
