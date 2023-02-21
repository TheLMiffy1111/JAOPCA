package thelm.jaopca.compat.universecraft;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "universeCraft")
public class UniverseCraftOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "universecraft";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("oreCondensed", "universeCraft:MetadataBlocks@2");
		api.registerOredict("oreSingularium", "universeCraft:MetadataBlocks@3");
		api.registerOredict("blockPaper", "universeCraft:MetadataBlocks@10");
		api.registerOredict("orePalladium", "universeCraft:MetadataBlocks2@8");
		api.registerOredict("oreRhodium", "universeCraft:MetadataBlocks2@9");
		api.registerOredict("oreZirconium", "universeCraft:MetadataBlocks2@10");
		api.registerOredict("ingotBlackholium", "universeCraft:Particle@42");
		api.registerOredict("ingotBlackholiumEnergised", "universeCraft:Particle@45");
	}
}
