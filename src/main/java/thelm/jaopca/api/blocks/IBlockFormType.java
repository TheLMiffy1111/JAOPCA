package thelm.jaopca.api.blocks;

import com.google.gson.JsonObject;

import thelm.jaopca.api.forms.IFormType;

public interface IBlockFormType extends IFormType<IBlockInfo> {

	@Override
	IBlockFormSettings getNewSettings();

	@Override
	IBlockFormSettings deserializeSettings(JsonObject jsonObject);
}
