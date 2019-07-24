package thelm.jaopca.client.events;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
import thelm.jaopca.modules.ModuleHandler;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		DeferredWorkQueue.runLater(()->{

		});
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		event.getMinecraftSupplier().get().getResourcePackList().addPackFinder(ResourceInjector.PackFinder.INSTANCE);
		DeferredWorkQueue.runLater(()->{
			ModuleHandler.onClientSetup(event);
		});
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ModelHandler.remapModels(event);
	}

	@SubscribeEvent
	public void onColorHandler(ColorHandlerEvent.Item event) {
		ColorHandler.setup(event);
	}
}
