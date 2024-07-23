package thelm.jaopca.compat.mekanism.slurries;

import java.util.function.Function;

import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryCreator;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;

class SlurryFormSettings implements ISlurryFormSettings {

	private ISlurryCreator slurryCreator = JAOPCASlurry::new;
	private Function<IMaterial, String> oreTagFunction = material->"";

	@Override
	public IFormType getType() {
		return SlurryFormType.INSTANCE;
	}

	@Override
	public ISlurryFormSettings setSlurryCreator(ISlurryCreator slurryCreator) {
		this.slurryCreator = slurryCreator;
		return this;
	}

	@Override
	public ISlurryCreator getSlurryCreator() {
		return slurryCreator;
	}

	@Override
	public ISlurryFormSettings setOreTagFunction(Function<IMaterial, String> oreTagFunction) {
		this.oreTagFunction = oreTagFunction;
		return this;
	}

	@Override
	public Function<IMaterial, String> getOreTagFunction() {
		return oreTagFunction;
	}
}
