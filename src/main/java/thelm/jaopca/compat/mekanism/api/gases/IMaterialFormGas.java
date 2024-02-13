package thelm.jaopca.compat.mekanism.api.gases;

import mekanism.api.gas.Gas;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormGas extends IMaterialForm {

	default Gas toGas() {
		return (Gas)this;
	}
}
