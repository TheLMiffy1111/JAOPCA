package thelm.jaopca.compat.mekanism.slurries;

import java.util.function.Supplier;

import thelm.jaopca.compat.mekanism.api.slurries.IMaterialFormSlurry;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryInfo;

record SlurryInfo(Supplier<IMaterialFormSlurry> slurry) implements ISlurryInfo {

	@Override
	public IMaterialFormSlurry getMaterialFormSlurry() {
		return slurry.get();
	}
}
