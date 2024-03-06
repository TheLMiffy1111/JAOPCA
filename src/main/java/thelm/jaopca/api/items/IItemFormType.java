package thelm.jaopca.api.items;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemFormType extends IFormType {

	@Override
	IItemFormSettings getNewSettings();

	@Override
	IItemInfo getMaterialFormInfo(IForm form, IMaterial material);
}
