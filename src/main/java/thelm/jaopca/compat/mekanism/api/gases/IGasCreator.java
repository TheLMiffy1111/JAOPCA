package thelm.jaopca.compat.mekanism.api.gases;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IGasCreator {

	IMaterialFormGas create(IForm form, IMaterial material, IGasFormSettings settings);
}
