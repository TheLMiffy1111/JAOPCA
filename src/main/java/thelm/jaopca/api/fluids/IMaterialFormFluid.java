package thelm.jaopca.api.fluids;

import net.minecraft.fluid.Fluid;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluid extends IMaterialForm {

	default Fluid asFluid() {
		return (Fluid)this;
	}
}
