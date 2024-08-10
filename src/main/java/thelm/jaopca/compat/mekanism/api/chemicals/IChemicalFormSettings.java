package thelm.jaopca.compat.mekanism.api.chemicals;

import java.util.function.Function;

import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IChemicalFormSettings extends IFormSettings {

	IChemicalFormSettings setChemicalCreator(IChemicalCreator chemicalCreator);

	IChemicalCreator getChemicalCreator();
	
	IChemicalFormSettings setOreTagFunction(Function<IMaterial, String> oreTagFunction);

	Function<IMaterial, String> getOreTagFunction();
}
