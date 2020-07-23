package thelm.jaopca.compat.mekanism.api.slurries;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface ISlurryCreator {

	IMaterialFormSlurry create(IForm form, IMaterial material, ISlurryFormSettings settings);
}
