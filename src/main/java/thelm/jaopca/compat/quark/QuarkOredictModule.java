package thelm.jaopca.compat.quark;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "quark")
public class QuarkOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "quark";
	}

	@Override
	public void register() {
		ApiImpl.INSTANCE.registerOredict("oreEnderBiotite", "quark:biotite_ore");
	}
}
