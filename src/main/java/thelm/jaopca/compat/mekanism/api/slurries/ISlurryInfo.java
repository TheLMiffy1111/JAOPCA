package thelm.jaopca.compat.mekanism.api.slurries;

import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.providers.ISlurryProvider;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface ISlurryInfo extends IMaterialFormInfo, ISlurryProvider {

	IMaterialFormSlurry getMaterialFormSlurry();

	@Override
	default Slurry getChemical() {
		return getMaterialFormSlurry().asSlurry();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormSlurry();
	}
}
