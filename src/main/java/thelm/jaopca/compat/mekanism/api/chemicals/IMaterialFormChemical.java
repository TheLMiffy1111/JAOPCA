package thelm.jaopca.compat.mekanism.api.chemicals;

import mekanism.api.chemical.Chemical;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormChemical extends IMaterialForm {

	default Chemical toChemical() {
		return (Chemical)this;
	}
}
