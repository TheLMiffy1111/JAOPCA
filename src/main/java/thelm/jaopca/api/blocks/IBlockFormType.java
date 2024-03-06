package thelm.jaopca.api.blocks;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockFormType extends IFormType {

	@Override
	IBlockFormSettings getNewSettings();

	@Override
	IBlockInfo getMaterialFormInfo(IForm form, IMaterial material);
}
