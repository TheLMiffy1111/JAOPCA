package thelm.jaopca.compat.crafttweaker;

import java.util.TreeMap;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;

import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.modules.ModuleHandler;

@ZenRegister
@ZenCodeType.Name("mods.jaopca.Module")
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

	@ZenCodeType.Getter("name")
	public String getName() {
		return module.getName();
	}

	@ZenCodeType.Getter("materialTypes")
	public String[] getMaterialTypes() {
		return module.getMaterialTypes().stream().map(MaterialType::getName).toArray(String[]::new);
	}

	@ZenCodeType.Getter("materials")
	public Material[] getMaterials() {
		return moduleData.getMaterials().stream().map(Material::getMaterialWrapper).toArray(Material[]::new);
	}

	@ZenCodeType.Method
	public boolean containsMaterial(Material material) {
		return moduleData.getMaterials().contains(material.getInternal());
	}

	@ZenCodeType.Getter("forms")
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
