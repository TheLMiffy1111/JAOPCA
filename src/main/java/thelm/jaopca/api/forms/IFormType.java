package thelm.jaopca.api.forms;

import java.util.Set;

import com.google.gson.JsonObject;

import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;

public interface IFormType<I extends IMaterialFormInfo<?>> extends Comparable<IFormType<?>> {

	String getName();

	String getTranslationKeyFormat();

	void addForm(IForm form);

	Set<IForm> getForms();

	boolean shouldRegister(IForm form, IMaterial material);

	IFormSettings getNewSettings();

	IFormSettings deserializeSettings(JsonObject jsonObject);

	I getMaterialFormInfo(IForm form, IMaterial material);

	@Override
	default int compareTo(IFormType<?> other) {
		return getName().compareTo(other.getName());
	}
}
