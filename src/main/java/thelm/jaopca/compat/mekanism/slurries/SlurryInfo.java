package thelm.jaopca.compat.mekanism.slurries;

import thelm.jaopca.compat.mekanism.api.slurries.IMaterialFormSlurry;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryInfo;

record SlurryInfo(IMaterialFormSlurry slurry) implements ISlurryInfo {

	@Override
	public IMaterialFormSlurry getMaterialFormSlurry() {
		return slurry;
	}
}
