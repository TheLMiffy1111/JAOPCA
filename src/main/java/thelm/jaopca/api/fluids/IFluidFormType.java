package thelm.jaopca.api.fluids;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IFormType;

public interface IFluidFormType extends IFormType<IFluidInfo> {

	@Override
	IFluidFormSettings getNewSettings();

	@Override
	IFluidFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);
}
