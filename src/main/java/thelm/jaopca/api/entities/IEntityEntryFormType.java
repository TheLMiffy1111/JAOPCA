package thelm.jaopca.api.entities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IFormType;

public interface IEntityEntryFormType extends IFormType {

	@Override
	IEntityEntryFormSettings getNewSettings();

	@Override
	IEntityEntryFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);
}
