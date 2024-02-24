package thelm.jaopca.compat.thermalfoundation;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "ThermalFoundation")
public class ThermalFoundationOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "thermalfoundation";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("dustMana", "ThermalFoundation:material@516");
	}
}
