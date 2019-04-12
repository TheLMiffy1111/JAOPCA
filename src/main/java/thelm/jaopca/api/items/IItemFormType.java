package thelm.jaopca.api.items;

import com.google.gson.JsonObject;

import thelm.jaopca.api.forms.IFormType;

public interface IItemFormType extends IFormType<IItemInfo> {

	@Override
	IItemFormSettings getNewSettings();

	@Override
	IItemFormSettings deserializeSettings(JsonObject jsonObject);
}
