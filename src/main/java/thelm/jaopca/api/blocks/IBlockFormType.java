package thelm.jaopca.api.blocks;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockFormType extends IFormType {

	@Override
	IBlockFormSettings getNewSettings();

	@Override
	IBlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	IBlockInfo getMaterialFormInfo(IForm form, IMaterial material);
}
