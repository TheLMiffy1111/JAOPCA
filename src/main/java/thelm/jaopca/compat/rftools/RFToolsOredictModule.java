package thelm.jaopca.compat.rftools;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "rftools")
public class RFToolsOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "rftools";
	}

	@Override
	public void register() {
		ApiImpl.INSTANCE.registerOredict("gemDimensionalShard", "rftools:dimensional_shard");
	}
}
