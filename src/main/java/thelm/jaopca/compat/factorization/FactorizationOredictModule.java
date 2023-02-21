package thelm.jaopca.compat.factorization;

import factorization.oreprocessing.ItemOreProcessing;
import factorization.shared.Core;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "factorization")
public class FactorizationOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "factorization";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ItemOreProcessing.OreType type = ItemOreProcessing.OreType.DARKIRON;
		api.registerOredict("dirtyGravelFzDarkIron", Core.registry.ore_dirty_gravel.makeStack(type));
		api.registerOredict("cleanGravelFzDarkIron", Core.registry.ore_clean_gravel.makeStack(type));
		api.registerOredict("reducedFzDarkIron", Core.registry.ore_reduced.makeStack(type));
		api.registerOredict("crystallineFzDarkIron", Core.registry.ore_crystal.makeStack(type));
	}
}
