package thelm.jaopca.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
import thelm.jaopca.localization.LocalizationRepoHandler;
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
		Minecraft mc = event.getMinecraftSupplier().get();
		mc.getResourcePackList().addPackFinder(ResourceInjector.PackFinder.INSTANCE);
		LocalizationRepoHandler.setup();
		((IReloadableResourceManager)mc.getResourceManager()).addReloadListener(new ReloadListener<Object>() {
			@Override
			protected Object prepare(IResourceManager resourceManager, IProfiler profiler) {
				return null;
			}
			@Override
			protected void apply(Object splashList, IResourceManager resourceManager, IProfiler profiler) {
				LocalizationRepoHandler.reload();
			}
		});
		DeferredWorkQueue.runLater(()->{
			ModuleHandler.onClientSetup(event);
		});
	}

	@SubscribeEvent
	public void onModelRegistry(ModelRegistryEvent event) {
		ModelHandler.registerModels();
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ModelHandler.remapModels(event);
	}

	@SubscribeEvent
	public void onColorHandler(ColorHandlerEvent.Item event) {
		ColorHandler.setup(event);
	}

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {

	}
}
