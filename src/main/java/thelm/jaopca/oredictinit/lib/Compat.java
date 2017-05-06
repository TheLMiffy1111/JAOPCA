package thelm.jaopca.oredictinit.lib;

import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.oredictinit.compat.CompatDraconicEvolution;
import thelm.jaopca.oredictinit.compat.CompatMekanism;

public class Compat {
	
	public static void init() {
		if(Loader.isModLoaded("Mekanism"))
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatMekanism());
		if(Loader.isModLoaded("draconicevolution")) {
			JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatDraconicEvolution());
		}
	}
}
