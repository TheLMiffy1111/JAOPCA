package thelm.jaopca;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thelm.jaopca.client.events.ClientEventHandler;
import thelm.jaopca.events.CommonEventHandler;

@Mod(JAOPCA.MOD_ID)
public class JAOPCA {

	public static final String MOD_ID = "jaopca";

	public JAOPCA() {
		FMLJavaModLoadingContext.get().getModEventBus().register(CommonEventHandler.getInstance());
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
			FMLJavaModLoadingContext.get().getModEventBus().register(ClientEventHandler.getInstance());
		});
	}
}
