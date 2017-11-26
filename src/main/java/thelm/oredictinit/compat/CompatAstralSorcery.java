package thelm.oredictinit.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import thelm.oredictinit.api.ICompat;

public class CompatAstralSorcery implements ICompat {

	@Override
	public String getName() {
		return "astralsorcery";
	}

	@Override
	public void register() {
		try {
			Class<?> modClass = Class.forName("hellfirepvp.astralsorcery.AstralSorcery");
			Field proxyField = modClass.getField("proxy");
			Object proxy = proxyField.get(null);
			Class<?> proxyClass = Class.forName("hellfirepvp.astralsorcery.common.CommonProxy");
			Method oreDictMethod = proxyClass.getDeclaredMethod("registerOreDictEntries");
			oreDictMethod.setAccessible(true);
			oreDictMethod.invoke(proxy);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
