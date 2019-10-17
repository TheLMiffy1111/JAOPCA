package thelm.jaopca.forms;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.modules.ModuleData;
import thelm.jaopca.modules.ModuleHandler;

public class Form implements IForm {

	private final IModule module;
	private final String name;
	private final IFormType type;
	private String secondaryName;
	private final EnumSet<MaterialType> materialTypes = EnumSet.allOf(MaterialType.class);
	private final TreeSet<String> defaultMaterialBlacklist = new TreeSet<>();
	private final TreeSet<String> materialBlacklist = new TreeSet<>();
	private final TreeSet<String> materialWhitelist = new TreeSet<>();
	private IFormSettings settings;
	private boolean skipGroupCheck = false;
	private boolean locked = false;
	private IFormRequest request;
	private final TreeSet<IMaterial> materials = new TreeSet<>();

	public Form(IModule module, String name, IFormType type) {
		this.module = Objects.requireNonNull(module);
		this.name = Objects.requireNonNull(name);
		this.type = Objects.requireNonNull(type);
		secondaryName = name;
		settings = type.getNewSettings();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IFormType getType() {
		return type;
	}

	@Override
	public IModule getModule() {
		return module;
	}

	@Override
	public IForm setSecondaryName(String secondaryName) {
		if(!locked) {
			this.secondaryName = secondaryName;
		}
		return this;
	}

	@Override
	public String getSecondaryName() {
		return secondaryName;
	}

	@Override
	public IForm setMaterialTypes(Collection<MaterialType> materialTypes) {
		if(!locked) {
			this.materialTypes.clear();
			this.materialTypes.addAll(materialTypes);
		}
		return this;
	}

	@Override
	public IForm setMaterialTypes(MaterialType... materialTypes) {
		if(!locked) {
			this.materialTypes.clear();
			Collections.addAll(this.materialTypes, materialTypes);
		}
		return this;
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return Collections.unmodifiableSet(materialTypes);
	}

	@Override
	public IForm setDefaultMaterialBlacklist(Collection<String> defaultMaterialBlacklist) {
		if(!locked) {
			this.defaultMaterialBlacklist.clear();
			this.defaultMaterialBlacklist.addAll(defaultMaterialBlacklist);
		}
		return this;
	}

	@Override
	public IForm setDefaultMaterialBlacklist(String... defaultMaterialBlacklist) {
		if(!locked) {
			this.defaultMaterialBlacklist.clear();
			Collections.addAll(this.defaultMaterialBlacklist, defaultMaterialBlacklist);
		}
		return this;
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return Collections.unmodifiableNavigableSet(defaultMaterialBlacklist);
	}

	@Override
	public IForm setSettings(IFormSettings settings) {
		if(!locked && settings.getType() == type) {
			this.settings = settings;
		}
		return this;
	}

	@Override
	public IFormSettings getSettings() {
		return settings;
	}

	@Override
	public IForm setSkipGroupedCheck(boolean skipGroupCheck) {
		if(!locked) {
			this.skipGroupCheck = skipGroupCheck;
		}
		return this;
	}

	@Override
	public boolean skipGroupedCheck() {
		return skipGroupCheck;
	}

	@Override
	public Set<IMaterial> getMaterials() {
		return Collections.unmodifiableNavigableSet(materials);
	}

	@Override
	public IForm lock() {
		locked = true;
		return this;
	}

	@Override
	public IFormRequest toRequest() {
		IFormRequest request = new FormRequest(module, this);
		setRequest(request);
		return request;
	}

	@Override
	public IForm setRequest(IFormRequest request) {
		if(request.getForms().contains(this)) {
			this.request = request;
		}
		return this;
	}

	@Override
	public boolean isMaterialValid(IMaterial material) {
		ModuleData data = ModuleHandler.getModuleData(module);
		return materialTypes.contains(material.getType()) &&
				!defaultMaterialBlacklist.contains(material.getName()) &&
				!material.getConfigModuleBlacklist().contains(module.getName()) &&
				!data.getConfigMaterialBlacklist().contains(material.getName()) &&
				type.shouldRegister(this, material) &&
				!data.getRejectedMaterials().contains(material) &&
				(!module.isPassive() ? true :
					data.getConfigPassiveMaterialWhitelist().contains(material.getName()) ||
					data.getRequestedMaterials().contains(material));
	}

	@Override
	public void setMaterials(Collection<IMaterial> materials) {
		this.materials.clear();
		this.materials.addAll(materials);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Form:"+name;
	}
}
