package thelm.oredictinit.compat;

import java.lang.reflect.Method;

import thelm.oredictinit.api.ICompat;

public class CompatNuclearCraft implements ICompat {

	@Override
	public String getName() {
		return "nuclearcraft";
	}

	@Override
	public void register() {
		try {
			Class<?> oreDictClass = Class.forName("nc.handler.OreDictHandler");
			Method initMethod = oreDictClass.getDeclaredMethod("registerOres");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
