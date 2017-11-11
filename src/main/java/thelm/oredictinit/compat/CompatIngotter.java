package thelm.oredictinit.compat;

import java.lang.reflect.Method;

import thelm.oredictinit.api.ICompat;

public class CompatIngotter implements ICompat {

	@Override
	public String getName() {
		return "ingotter";
	}

	@Override
	public void register() {
		try {
			Class<?> oreDictClass = Class.forName("terrails.ingotter.init.ModOreDictionary");
			Method initMethod = oreDictClass.getMethod("init");
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
