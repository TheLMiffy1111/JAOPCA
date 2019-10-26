package thelm.jaopca.compat.mekanism.api.gases;

import mekanism.api.gas.Gas;
import mekanism.api.providers.IGasProvider;
import net.minecraft.fluid.Fluid;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IGasInfo extends IMaterialFormInfo, IGasProvider {

	IMaterialFormGas getMaterialFormGas();

	@Override
	default Gas getGas() {
		return getMaterialFormGas().asGas();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormGas();
	}
}
