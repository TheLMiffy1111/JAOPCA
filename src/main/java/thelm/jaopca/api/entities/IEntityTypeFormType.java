package thelm.jaopca.api.entities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IFormType;

public interface IEntityTypeFormType extends IFormType {

	@Override
	IEntityTypeFormSettings getNewSettings();

	@Override
	IEntityTypeFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);
}
