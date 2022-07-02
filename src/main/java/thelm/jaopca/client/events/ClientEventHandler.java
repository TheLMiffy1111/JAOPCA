package thelm.jaopca.client.events;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.models.fluids.TexturedFluidModel;
import thelm.jaopca.client.resources.ResourceHandler;
import thelm.jaopca.events.CommonEventHandler;

public class ClientEventHandler extends CommonEventHandler {

	static {
		ModelLoaderRegistry.registerLoader(TexturedFluidModel.Loader.INSTANCE);
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		super.onInit(event);
	}

	@SubscribeEvent
	public void onModelRegistry(ModelRegistryEvent event) {
		ModelHandler.registerModels();
	}

	@SubscribeEvent
	public void onColorHandler(ColorHandlerEvent.Item event) {
		ColorHandler.setup(event);
	}

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		ResourceHandler.registerTextures(event.getMap());
	}
}
