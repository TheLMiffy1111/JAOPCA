package thelm.jaopca.oredictinit.lib;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.oredictinit.compat.CompatMekanism;

public class Compat {
	
	public static void init() {
		JAOPCAApi.ORE_DICT_COMPAT_LIST.add(new CompatMekanism());
	}
}
