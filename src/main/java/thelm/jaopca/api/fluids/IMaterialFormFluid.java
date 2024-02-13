package thelm.jaopca.api.fluids;

import net.minecraftforge.fluids.Fluid;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluid extends IMaterialForm {

	default Fluid toFluid() {
		return (Fluid)this;
	}
}
