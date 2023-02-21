package thelm.jaopca.compat.elysium;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "elysium")
public class ElysiumOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "elysium";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("blockCobalt", "elysium:tile.blockCobalt");
		api.registerOredict("blockSilicon", "elysium:tile.blockSilicon");
		api.registerOredict("blockCobalt", "elysium:tile.blockCobalt");
		api.registerOredict("blockSulfur", "elysium:tile.blockSulphur");
		api.registerOredict("blockTourmaline", "elysium:tile.blockTourmaline");
		api.registerOredict("blockJade", "elysium:tile.blockJade");
		api.registerOredict("blockIridium", "elysium:tile.blockIridium");
		api.registerOredict("blockCobalt", "elysium:tile.blockCobalt");
		api.registerOredict("oreSulfur", "elysium:tile.oreSulphur");
		api.registerOredict("blockBeryl", "elysium:tile.blockBeryl");
		api.registerOredict("dustSulfur", "elysium:item.elysium_sulphur");
		api.registerOredict("ingotIridium", "elysium:item.ingotIridium");
		api.registerOredict("gemSilicon", "elysium:item.siliconchunk");
		api.registerOredict("ingotCobalt", "elysium:item.ingotCobalt");
		api.registerOredict("gemTourmaline", "elysium:item.tourmaline");
		api.registerOredict("gemJade", "elysium:item.jade");
		api.registerOredict("ingotTooth", "elysium:item.ingotTooth");
		api.registerOredict("gemBeryl", "elysium:item.beryl");
	}
}
