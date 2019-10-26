package thelm.jaopca.compat.mekanism.api.gases;

import mekanism.api.gas.Gas;
import mekanism.api.text.IHasTextComponent;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormGas extends IMaterialForm {

	default Gas asGas() {
		return (Gas)this;
	}
}
