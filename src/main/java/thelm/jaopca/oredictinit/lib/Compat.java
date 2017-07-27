package thelm.jaopca.oredictinit.lib;

import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.oredictinit.compat.CompatCalculator;
import thelm.jaopca.oredictinit.compat.CompatDraconicEvolution;
import thelm.jaopca.oredictinit.compat.CompatExtremeReactors;
import thelm.jaopca.oredictinit.compat.CompatGalacticraftCore;
import thelm.jaopca.oredictinit.compat.CompatGalacticraftPlanets;
import thelm.jaopca.oredictinit.compat.CompatMekanism;
import thelm.jaopca.oredictinit.compat.CompatNuclearCraft;
import thelm.jaopca.oredictinit.compat.CompatTheBetweenlands;
import thelm.jaopca.oredictinit.compat.CompatTinkersConstruct;

public class Compat {

	public static void init() {
		if(Loader.isModLoaded("mekanism")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatMekanism());
		}
		if(Loader.isModLoaded("draconicevolution")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatDraconicEvolution());
		}
		if(Loader.isModLoaded("galacticraftcore")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatGalacticraftCore());
		}
		if(Loader.isModLoaded("galacticraftplanets")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatGalacticraftPlanets());
		}
		if(Loader.isModLoaded("nuclearcraft")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatNuclearCraft());
		}
		if(Loader.isModLoaded("thebetweenlands")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatTheBetweenlands());
		}
		if(Loader.isModLoaded("calculator")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatCalculator());
		}
		if(Loader.isModLoaded("bigreactors")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatExtremeReactors());
		}
		if(Loader.isModLoaded("tconstruct")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatTinkersConstruct());
		}
	}
}
