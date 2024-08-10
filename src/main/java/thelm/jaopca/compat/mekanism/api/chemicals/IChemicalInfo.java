package thelm.jaopca.compat.mekanism.api.chemicals;

import mekanism.api.chemical.Chemical;
import mekanism.api.providers.IChemicalProvider;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IChemicalInfo extends IMaterialFormInfo, IChemicalProvider {

	IMaterialFormChemical getMaterialFormChemical();

	@Override
	default Chemical getChemical() {
		return getMaterialFormChemical().toChemical();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormChemical();
	}
}
