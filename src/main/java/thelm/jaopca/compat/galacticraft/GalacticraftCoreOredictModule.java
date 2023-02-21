package thelm.jaopca.compat.galacticraft;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "GalacticraftCore")
public class GalacticraftCoreOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "galacticraftcore";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("oreMeteoricIron", "GalacticraftCore:tile.fallenMeteor@0");
		api.registerOredict("blockMeteoricIron", "GalacticraftCore:tile.gcBlockCore@12");
		api.registerOredict("gemSilicon", "GalacticraftCore:item.basicItem@2");
		api.registerOredict("crystalCheese", "GalacticraftCore:item.cheeseCurd");
	}
}
