package thelm.jaopca.compat.mekanism.chemicals;

import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalInfo;
import thelm.jaopca.compat.mekanism.api.chemicals.IMaterialFormChemical;

record ChemicalInfo(IMaterialFormChemical chemical) implements IChemicalInfo {

	@Override
	public IMaterialFormChemical getMaterialFormChemical() {
		return chemical;
	}
}
