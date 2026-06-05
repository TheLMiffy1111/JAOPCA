package thelm.jaopca.api.fluids;

import net.neoforged.neoforge.fluids.FluidType;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidType extends IMaterialForm {

	default FluidType toFluidType() {
		return (FluidType)this;
	}
}
