package thelm.jaopca.api.fluids;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluid extends IMaterialForm {

	default Fluid toFluid() {
		return (Fluid)this;
	}

	FluidState getSourceState();
}
