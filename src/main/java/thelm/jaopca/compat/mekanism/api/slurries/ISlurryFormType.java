package thelm.jaopca.compat.mekanism.api.slurries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface ISlurryFormType extends IFormType {

	@Override
	ISlurryFormSettings getNewSettings();

	@Override
	ISlurryFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	ISlurryInfo getMaterialFormInfo(IForm form, IMaterial material);
}
