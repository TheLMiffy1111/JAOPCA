package thelm.oredictinit.compat;

import java.lang.reflect.Method;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.oredictinit.api.ICompat;

public class CompatTinkersConstruct implements ICompat {

	@Override
	public String getName() {
		return "tconstruct";
	}

	@Override
	public void register() {
		try {
			Class<?> oreDictClass = Class.forName("slimeknights.tconstruct.common.TinkerOredict");
			Method oreDictMethod = oreDictClass.getDeclaredMethod("doTheOredict", FMLInitializationEvent.class);
			oreDictMethod.setAccessible(true);
			oreDictMethod.invoke(null, new Object[] {null});
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
