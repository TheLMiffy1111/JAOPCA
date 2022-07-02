package thelm.jaopca.compat.mekanism.api.gases;

import thelm.jaopca.api.forms.IFormSettings;

public interface IGasFormSettings extends IFormSettings {

	IGasFormSettings setGasCreator(IGasCreator gasCreator);

	IGasCreator getGasCreator();

	IGasFormSettings setIsHidden(boolean isVisible);

	boolean getIsHidden();
}
