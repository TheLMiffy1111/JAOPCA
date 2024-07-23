package thelm.jaopca;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thelm.jaopca.events.CommonEventHandler;

@Mod(JAOPCA.MOD_ID)
public class JAOPCA {

	public static final String MOD_ID = "jaopca";
	public static boolean mixinLoaded = false;

	public JAOPCA(IEventBus modEventBus) {
		assert mixinLoaded;
		modEventBus.register(CommonEventHandler.getInstance());
	}
}
