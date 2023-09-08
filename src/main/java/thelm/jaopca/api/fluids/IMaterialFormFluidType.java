package thelm.jaopca.api.fluids;

import net.minecraftforge.fluids.FluidType;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidType extends IMaterialForm {

	default FluidType toFluidType() {
		return (FluidType)this;
	}
}
