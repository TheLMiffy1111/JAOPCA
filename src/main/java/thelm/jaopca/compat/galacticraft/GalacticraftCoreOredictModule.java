package thelm.jaopca.compat.galacticraft;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "galacticraftcore")
public class GalacticraftCoreOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "galacticraftcore";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("oreMeteoricIron", "galacticraftcore:fallen_meteor");
		api.registerOredict("blockMeteoricIron", "galacticraftcore:basic_block_core@12");
		api.registerOredict("plateMeteoricIron", "galacticraftcore:item_basic_moon@1");
		api.registerOredict("oreLunarSapphire", "galacticraftcore:basic_block_moon@6");
		api.registerOredict("gemLunarSapphire", "galacticraftcore:item_basic_moon@2");
		api.registerOredict("gemSilicon", "galacticraftcore:basic_item@2");
		api.registerOredict("crystalCheese", "galacticraftcore:cheese_curd");
	}
}
