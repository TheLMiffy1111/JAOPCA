package thelm.jaopca.compat.galaxyspace;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "galaxyspace")
public class GalaxySpaceOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "galaxyspace";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("blockCobalt", "galaxyspace:decoblocks@4");
		api.registerOredict("blockNickel", "galaxyspace:decoblocks@5");
		api.registerOredict("blockMagnesium", "galaxyspace:decoblocks@6");
		api.registerOredict("oreMeteoricIron", "galaxyspace:ceresblocks@3");
		api.registerOredict("oreIron", "galaxyspace:phobosblocks@2");
		api.registerOredict("oreMeteoricIron", "galaxyspace:phobosblocks@3");
		api.registerOredict("oreNickel", "galaxyspace:phobosblocks@4");
		api.registerOredict("oreDesh", "galaxyspace:phobosblocks@5");
		api.registerOredict("oreTitanium", "galaxyspace:ganymedeblocks@3");
		api.registerOredict("gemDolomite", "galaxyspace:gs_basic@3");
		api.registerOredict("gemVolcanic", "galaxyspace:gs_basic@12");
	}
}
