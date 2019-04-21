package thelm.jaopca.api.forms;

import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;

public interface IFormType<I extends IMaterialFormInfo<?>> extends Comparable<IFormType<?>> {

	String getName();

	String getTranslationKeyFormat();

	void addForm(IForm form);

	Set<IForm> getForms();

	boolean shouldRegister(IForm form, IMaterial material);

	IFormSettings getNewSettings();

	GsonBuilder configureGsonBuilder(GsonBuilder builder);

	IFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	I getMaterialFormInfo(IForm form, IMaterial material);

	@Override
	default int compareTo(IFormType<?> other) {
		return getName().compareTo(other.getName());
	}
}
