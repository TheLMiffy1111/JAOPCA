package thelm.jaopca.compat.mekanism.api.gases;

import mekanism.api.chemical.gas.Gas;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormGas extends IMaterialForm {

	default Gas asGas() {
		return (Gas)this;
	}
}
