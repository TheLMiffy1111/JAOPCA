package thelm.jaopca.compat.mekanism.gases;

import thelm.jaopca.api.blocks.IBlockCreator;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.compat.mekanism.api.gases.IGasCreator;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormSettings;

public class GasFormSettings implements IGasFormSettings {
	
	GasFormSettings() {}
	
	private IGasCreator gasCreator = JAOPCAGas::new;
	private boolean isVisible = true;
	
	@Override
	public IFormType getType() {
		return GasFormType.INSTANCE;
	}

	@Override
	public IGasFormSettings setGasCreator(IGasCreator gasCreator) {
		this.gasCreator = gasCreator;
		return this;
	}

	@Override
	public IGasCreator getGasCreator() {
		return gasCreator;
	}

	@Override
	public IGasFormSettings setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	@Override
	public boolean getIsVisible() {
		return isVisible;
	}
}
