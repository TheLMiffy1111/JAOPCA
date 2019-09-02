package thelm.jaopca.api.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemFormType extends IFormType {

	@Override
	IItemFormSettings getNewSettings();

	@Override
	IItemFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	IItemInfo getMaterialFormInfo(IForm form, IMaterial material);
}
