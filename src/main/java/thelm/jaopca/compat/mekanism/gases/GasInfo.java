package thelm.jaopca.compat.mekanism.gases;

import thelm.jaopca.compat.mekanism.api.gases.IGasInfo;
import thelm.jaopca.compat.mekanism.api.gases.IMaterialFormGas;

class GasInfo implements IGasInfo {

	private final IMaterialFormGas gas;

	GasInfo(IMaterialFormGas gas) {
		this.gas = gas;
	}

	@Override
	public IMaterialFormGas getMaterialFormGas() {
		return gas;
	}
}
