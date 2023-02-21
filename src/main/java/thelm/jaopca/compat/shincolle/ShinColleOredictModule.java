package thelm.jaopca.compat.shincolle;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "shincolle")
public class ShinColleOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "shincolle";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("blockManganese", "shincolle:BlockPolymetal");
		api.registerOredict("blockAbyssium", "shincolle:BlockAbyssium");
		api.registerOredict("oreManganese", "shincolle:BlockPolymetalOre");
		api.registerOredict("blockGrudge", "shincolle:BlockGrudge");
		api.registerOredict("blockGrudgeHeavy", "shincolle:BlockGrudgeHeavy");
		api.registerOredict("dustGrudge", "shincolle:Grudge");
		api.registerOredict("nuggetAbyssium", "shincolle:AbyssNugget@0");
		api.registerOredict("dustTinyManganese", "shincolle:AbyssNugget@1");			
		api.registerOredict("ingotAbyssium", "shincolle:AbyssMetal@0");
		api.registerOredict("dustManganese", "shincolle:AbyssMetal@1");
	}
}
