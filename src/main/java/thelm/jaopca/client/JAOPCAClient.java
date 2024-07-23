package thelm.jaopca.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.client.events.ClientEventHandler;

@Mod(value = JAOPCA.MOD_ID, dist = Dist.CLIENT)
public class JAOPCAClient {

	public JAOPCAClient(IEventBus modEventBus) {
		modEventBus.register(ClientEventHandler.getInstance());
	}
}
