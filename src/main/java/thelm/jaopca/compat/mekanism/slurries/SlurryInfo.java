package thelm.jaopca.compat.mekanism.slurries;

import thelm.jaopca.compat.mekanism.api.slurries.IMaterialFormSlurry;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryInfo;

public class SlurryInfo implements ISlurryInfo {

	private final IMaterialFormSlurry slurry;

	SlurryInfo(IMaterialFormSlurry slurry) {
		this.slurry = slurry;
	}

	@Override
	public IMaterialFormSlurry getMaterialFormSlurry() {
		return slurry;
	}
}
