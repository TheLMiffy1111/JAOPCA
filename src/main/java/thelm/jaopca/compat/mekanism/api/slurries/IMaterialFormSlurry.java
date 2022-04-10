package thelm.jaopca.compat.mekanism.api.slurries;

import mekanism.api.chemical.slurry.Slurry;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormSlurry extends IMaterialForm {

	default Slurry asSlurry() {
		return (Slurry)this;
	}
}
