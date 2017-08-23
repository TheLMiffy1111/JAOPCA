package thelm.oredictinit.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import thelm.jaopca.api.ICompat;

public class CompatExtremeReactors implements ICompat {

	@Override
	public String getName() {
		return "extremereactors";
	}

	@Override
	public void register() {
		try {
			Class<?> initClass = Class.forName("erogenousbeef.bigreactors.init.InitHandler");
			Class<?> objClass = Class.forName("it.zerono.mods.zerocore.lib.IGameObject");
			Field instanceField = initClass.getField("INSTANCE");
			Field objListField = initClass.getDeclaredField("_objects");
			objListField.setAccessible(true);
			Method registerMethod = objClass.getMethod("registerOreDictionaryEntries");
			Object instance = instanceField.get(null);
			List<?> objList = (List<?>)objListField.get(instance);
			for(Object obj : objList) {
				registerMethod.invoke(obj);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
