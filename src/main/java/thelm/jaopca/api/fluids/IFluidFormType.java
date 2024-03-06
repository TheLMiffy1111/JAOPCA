package thelm.jaopca.api.fluids;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IFluidFormType extends IFormType {

	@Override
	IFluidFormSettings getNewSettings();

	@Override
	IFluidInfo getMaterialFormInfo(IForm form, IMaterial material);
}
