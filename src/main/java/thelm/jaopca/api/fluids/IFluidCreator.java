package thelm.jaopca.api.fluids;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IFluidCreator {

	IMaterialFormFluid create(IForm form, IMaterial material, IFluidFormSettings settings);
}
