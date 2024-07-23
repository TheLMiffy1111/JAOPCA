package thelm.jaopca.compat.mekanism.slurries;

import java.util.function.Function;

import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryCreator;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;

public class SlurryFormSettings implements ISlurryFormSettings {

	SlurryFormSettings() {}

	private ISlurryCreator slurryCreator = JAOPCASlurry::new;
	private boolean isHidden = false;
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
	public ISlurryFormSettings setIsHidden(boolean isHidden) {
		this.isHidden = isHidden;
		return this;
	}

	@Override
	public boolean getIsHidden() {
		return isHidden;
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
