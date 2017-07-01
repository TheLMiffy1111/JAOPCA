package thelm.jaopca.oredictinit.compat;

import java.lang.reflect.Method;

import thelm.jaopca.api.ICompat;

public class CompatDraconicEvolution implements ICompat {

	@Override
	public void register() {
		try {
			Class<?> oreDictClass = Class.forName("com.brandon3055.draconicevolution.OreHandler");
			Method initMethod = oreDictClass.getDeclaredMethod("initialize");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "draconicevolution";
	}
}
