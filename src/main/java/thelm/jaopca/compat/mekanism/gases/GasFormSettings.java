package thelm.jaopca.compat.mekanism.gases;

import thelm.jaopca.api.blocks.IBlockCreator;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.compat.mekanism.api.gases.IGasCreator;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormSettings;

public class GasFormSettings implements IGasFormSettings {
	
	GasFormSettings() {}
	
	private IGasCreator gasCreator = JAOPCAGas::new;
	private boolean isHidden = false;
	
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
	public IGasFormSettings setIsHidden(boolean isHidden) {
		this.isHidden = isHidden;
		return this;
	}

	@Override
	public boolean getIsHidden() {
		return isHidden;
	}
}
