package thelm.jaopca.compat.mekanism.api.chemicals;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public interface IChemicalFormType extends IFormType {

	@Override
	IChemicalFormSettings getNewSettings();

	@Override
	IChemicalInfo getMaterialFormInfo(IForm form, IMaterial material);
}
