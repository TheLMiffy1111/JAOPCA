package thelm.jaopca.api.fluids;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IFluidFormType extends IFormType {

	@Override
	IFluidFormSettings getNewSettings();

	@Override
	IFluidFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	IFluidInfo getMaterialFormInfo(IForm form, IMaterial material);
}
