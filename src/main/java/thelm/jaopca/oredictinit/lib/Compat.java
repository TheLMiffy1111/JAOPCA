package thelm.jaopca.oredictinit.lib;

import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.oredictinit.compat.CompatDraconicEvolution;
import thelm.jaopca.oredictinit.compat.CompatGalacticraftCore;
import thelm.jaopca.oredictinit.compat.CompatGalacticraftPlanets;
import thelm.jaopca.oredictinit.compat.CompatMekanism;
import thelm.jaopca.oredictinit.compat.CompatNuclearCraft;

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
	}
}
