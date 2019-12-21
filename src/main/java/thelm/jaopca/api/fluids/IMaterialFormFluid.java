package thelm.jaopca.api.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IntegerProperty;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluid extends IMaterialForm {

	default Fluid asFluid() {
		return (Fluid)this;
	}

	IFluidState getSourceState();
}
