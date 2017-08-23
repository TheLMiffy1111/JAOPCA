package thelm.oredictinit.lib;

import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.JAOPCAApi;
import thelm.oredictinit.compat.CompatCalculator;
import thelm.oredictinit.compat.CompatDraconicEvolution;
import thelm.oredictinit.compat.CompatExtremeReactors;
import thelm.oredictinit.compat.CompatGalacticraftCore;
import thelm.oredictinit.compat.CompatGalacticraftPlanets;
import thelm.oredictinit.compat.CompatMekanism;
import thelm.oredictinit.compat.CompatNuclearCraft;
import thelm.oredictinit.compat.CompatTheBetweenlands;
import thelm.oredictinit.compat.CompatTinkersConstruct;

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
