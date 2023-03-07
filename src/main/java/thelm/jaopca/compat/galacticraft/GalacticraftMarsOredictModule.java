package thelm.jaopca.compat.galacticraft;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "GalacticraftMars")
public class GalacticraftMarsOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "galacticraftmars";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("blockDesh", "GalacticraftMars:tile.mars@8");
		api.registerOredict("oreTitanium", "GalacticraftMars:tile.asteroidsBlock@4");
		api.registerOredict("ingotDesh", "GalacticraftMars:item.null@2");
	}
}
