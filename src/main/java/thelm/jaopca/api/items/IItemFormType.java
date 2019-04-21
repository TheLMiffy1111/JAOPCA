package thelm.jaopca.api.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IFormType;

public interface IItemFormType extends IFormType<IItemInfo> {

	@Override
	IItemFormSettings getNewSettings();

	@Override
	IItemFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);
}
