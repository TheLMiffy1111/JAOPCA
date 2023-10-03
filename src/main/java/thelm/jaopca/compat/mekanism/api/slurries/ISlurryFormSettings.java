package thelm.jaopca.compat.mekanism.api.slurries;

import thelm.jaopca.api.forms.IFormSettings;

public interface ISlurryFormSettings extends IFormSettings {

	ISlurryFormSettings setSlurryCreator(ISlurryCreator slurryCreator);

	ISlurryCreator getSlurryCreator();
	
	ISlurryFormSettings setIsHidden(boolean isVisible);
	
	boolean getIsHidden();
}
