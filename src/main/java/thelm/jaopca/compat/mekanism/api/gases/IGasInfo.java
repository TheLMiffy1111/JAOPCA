package thelm.jaopca.compat.mekanism.api.gases;

import mekanism.api.gas.Gas;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IGasInfo extends IMaterialFormInfo, IGasProvider {

	IMaterialFormGas getMaterialFormGas();

	@Override
	default Gas asGas() {
		return getMaterialFormGas().asGas();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormGas();
	}
}
