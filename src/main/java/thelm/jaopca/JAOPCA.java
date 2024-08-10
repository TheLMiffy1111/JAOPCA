package thelm.jaopca;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thelm.jaopca.events.CommonEventHandler;

@Mod(JAOPCA.MOD_ID)
public class JAOPCA {

	public static final String MOD_ID = "jaopca";

	public JAOPCA(IEventBus modEventBus) {
		modEventBus.register(CommonEventHandler.getInstance());
	}
}
