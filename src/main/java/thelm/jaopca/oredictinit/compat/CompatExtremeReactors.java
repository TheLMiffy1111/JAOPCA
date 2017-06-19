package thelm.jaopca.oredictinit.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
			Field instanceField = initClass.getField("INSTANCE");
			Method initMethod = initClass.getMethod("onInit", FMLInitializationEvent.class);
			Object instance = instanceField.get(null);
			initMethod.invoke(instance, (Object[])null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
