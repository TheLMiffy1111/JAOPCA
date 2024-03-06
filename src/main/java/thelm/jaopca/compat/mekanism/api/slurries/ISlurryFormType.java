package thelm.jaopca.compat.mekanism.api.slurries;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface ISlurryFormType extends IFormType {

	@Override
	ISlurryFormSettings getNewSettings();

	@Override
	ISlurryInfo getMaterialFormInfo(IForm form, IMaterial material);
}
