package thelm.jaopca.compat.mekanism.api.gases;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IGasFormType extends IFormType {

	@Override
	IGasFormSettings getNewSettings();

	@Override
	IGasFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	IGasInfo getMaterialFormInfo(IForm form, IMaterial material);
}
