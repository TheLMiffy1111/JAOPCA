package thelm.jaopca.api.forms;

import java.util.Collection;
import java.util.Set;

import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;

public interface IForm extends Comparable<IForm> {

	String getName();

	IFormType getType();

	IModule getModule();

	IForm setSecondaryName(String secondaryName);

	String getSecondaryName();

	IForm setMaterialTypes(Collection<MaterialType> materialTypes);

	IForm setMaterialTypes(MaterialType... materialTypes);

	Set<MaterialType> getMaterialTypes();

	IForm setDefaultMaterialBlacklist(Collection<String> defaultMaterialBlacklist);

	IForm setDefaultMaterialBlacklist(String... defaultMaterialBlacklist);

	Set<String> getDefaultMaterialBlacklist();

	IForm setSettings(IFormSettings settings);

	IFormSettings getSettings();

	IForm setSkipGroupedCheck(boolean skipGroupCheck);

	boolean skipGroupedCheck();

	Set<IMaterial> getMaterials();

	Set<IMaterial> getFilteredMaterials();

	IForm lock();

	IFormRequest toRequest();

	IForm setRequest(IFormRequest request);

	boolean isMaterialValid(IMaterial material);

	void setMaterials(Collection<IMaterial> materials);

	@Override
	default int compareTo(IForm other) {
		return getName().compareTo(other.getName());
	}
}
