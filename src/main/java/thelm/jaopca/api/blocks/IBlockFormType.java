package thelm.jaopca.api.blocks;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IFormType;

public interface IBlockFormType extends IFormType<IBlockInfo> {

	@Override
	IBlockFormSettings getNewSettings();

	@Override
	IBlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);
}
