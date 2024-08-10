package thelm.jaopca.compat.mekanism.chemicals;

import java.util.function.Function;

import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalCreator;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalFormSettings;

class ChemicalFormSettings implements IChemicalFormSettings {

	private IChemicalCreator chemicalCreator = JAOPCAChemical::new;
	private Function<IMaterial, String> oreTagFunction = material->"";

	@Override
	public IFormType getType() {
		return ChemicalFormType.INSTANCE;
	}

	@Override
	public IChemicalFormSettings setChemicalCreator(IChemicalCreator chemicalCreator) {
		this.chemicalCreator = chemicalCreator;
		return this;
	}

	@Override
	public IChemicalCreator getChemicalCreator() {
		return chemicalCreator;
	}

	@Override
	public IChemicalFormSettings setOreTagFunction(Function<IMaterial, String> oreTagFunction) {
		this.oreTagFunction = oreTagFunction;
		return this;
	}

	@Override
	public Function<IMaterial, String> getOreTagFunction() {
		return oreTagFunction;
	}
}
