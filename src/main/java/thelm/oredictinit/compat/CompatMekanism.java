package thelm.oredictinit.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import thelm.jaopca.api.ICompat;

public class CompatMekanism implements ICompat {

	@Override
	public void register() {
		try {
			Class<?> mekanismClass = Class.forName("mekanism.common.Mekanism");
			Field instanceField = mekanismClass.getDeclaredField("instance");
			instanceField.setAccessible(true);
			Method oreDictMethod = mekanismClass.getDeclaredMethod("registerOreDict");
			oreDictMethod.setAccessible(true);
			oreDictMethod.invoke(instanceField.get(null));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "mekanism";
	}
}
