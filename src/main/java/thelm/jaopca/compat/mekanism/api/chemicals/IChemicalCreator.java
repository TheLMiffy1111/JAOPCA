package thelm.jaopca.compat.mekanism.api.chemicals;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IChemicalCreator {

	IMaterialFormChemical create(IForm form, IMaterial material, IChemicalFormSettings settings);
}
