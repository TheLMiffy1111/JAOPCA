package thelm.jaopca.api.forms;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Multimap;

import thelm.jaopca.api.materials.EnumMaterialType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;

public interface IForm extends Comparable<IForm> {

	String getName();

	IFormType<?> getType();

	IModule getModule();

	IForm setSecondaryName(String secondaryName);

	String getSecondaryName();

	IForm setTranslationKey(String translationKey);

	String getTranslationKey();

	IForm setMaterialTypes(EnumMaterialType... materialTypes);

	Set<EnumMaterialType> getMaterialTypes();

	IForm setDefaultMaterialBlacklist(String... defaultMaterialBlacklist);

	Set<String> getDefaultMaterialBlacklist();

	IForm setSettings(IFormSettings settings);

	IFormSettings getSettings();

	IForm setSkipGroupedCheck(boolean skipGroupCheck);

	boolean skipGroupedCheck();

	Set<IMaterial> getMaterials();

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
