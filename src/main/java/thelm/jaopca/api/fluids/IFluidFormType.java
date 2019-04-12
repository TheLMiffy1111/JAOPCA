package thelm.jaopca.api.fluids;

import com.google.gson.JsonObject;

import thelm.jaopca.api.forms.IFormType;

public interface IFluidFormType extends IFormType<IFluidInfo> {

	@Override
	IFluidFormSettings getNewSettings();

	@Override
	IFluidFormSettings deserializeSettings(JsonObject jsonObject);
}
