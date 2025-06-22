package thelm.jaopca.compat.mekanism.chemicals;

import java.util.function.Supplier;

import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalInfo;
import thelm.jaopca.compat.mekanism.api.chemicals.IMaterialFormChemical;

record ChemicalInfo(Supplier<IMaterialFormChemical> chemical) implements IChemicalInfo {

	@Override
	public IMaterialFormChemical getMaterialFormChemical() {
		return chemical.get();
	}
}
