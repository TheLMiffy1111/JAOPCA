package thelm.jaopca;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import thelm.jaopca.client.events.ClientEventHandler;
import thelm.jaopca.events.CommonEventHandler;
import thelm.jaopca.utils.MiscHelper;

@Mod(JAOPCA.MOD_ID)
public class JAOPCA {

	public static final String MOD_ID = "jaopca";
	public static boolean mixinLoaded = false;

	public JAOPCA(IEventBus modEventBus) {
		assert mixinLoaded;
		modEventBus.register(CommonEventHandler.getInstance());
		MiscHelper.INSTANCE.conditionalRunnable(FMLEnvironment.dist::isClient, ()->()->{
			modEventBus.register(ClientEventHandler.getInstance());
		}, ()->()->{}).run();
	}
}
