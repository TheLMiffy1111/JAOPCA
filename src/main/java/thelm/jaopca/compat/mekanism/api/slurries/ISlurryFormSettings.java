package thelm.jaopca.compat.mekanism.api.slurries;

import java.util.function.Function;

import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface ISlurryFormSettings extends IFormSettings {

	ISlurryFormSettings setSlurryCreator(ISlurryCreator slurryCreator);

	ISlurryCreator getSlurryCreator();
	
	ISlurryFormSettings setOreTagFunction(Function<IMaterial, String> oreTagFunction);

	Function<IMaterial, String> getOreTagFunction();
}
