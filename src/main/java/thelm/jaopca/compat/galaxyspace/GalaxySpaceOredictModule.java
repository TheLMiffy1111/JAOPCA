package thelm.jaopca.compat.galaxyspace;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "GalaxySpace")
public class GalaxySpaceOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "galaxyspace";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("oreSapphire", "GalaxySpace:ores@2");
		api.registerOredict("blockSapphire", "GalaxySpace:ores@4");
		api.registerOredict("oreTitanium", "GalaxySpace:ganymedeblocks@3");
		api.registerOredict("oreMeteoricIron", "GalaxySpace:ceresblocks@3");
		api.registerOredict("gemDolomite", "GalaxySpace:item.BasicItems@4");
		api.registerOredict("gemSapphire", "GalaxySpace:item.BasicItems@6");
		api.registerOredict("dustGlowstone", "GalaxySpace:item.GlowstoneDusts@0");
		api.registerOredict("dustGlowstone", "GalaxySpace:item.GlowstoneDusts@1");
	}
}
