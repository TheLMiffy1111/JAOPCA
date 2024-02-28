package thelm.jaopca.client.events;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.localization.LocalizationRepoHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggingOut);
		LocalizationRepoHandler.setup();
		for(IMaterialFormFluid fluid : FluidFormType.getFluids()) {
			ItemBlockRenderTypes.setRenderLayer(fluid.toFluid(), RenderType.translucent());
		}
		ModuleHandler.onClientSetup(event);
	}

	@SubscribeEvent
	public void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new SimplePreparableReloadListener<>() {
			@Override
			protected Object prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
				return null;
			}
			@Override
			protected void apply(Object splashList, ResourceManager resourceManager, ProfilerFiller profiler) {
				LocalizationRepoHandler.reload();
			}
		});
	}

	@SubscribeEvent
	public void onModelRegisterAdditional(ModelEvent.RegisterAdditional event) {
		ModelHandler.registerModels(event);
	}

	@SubscribeEvent
	public void onModelBakingCompleted(ModelEvent.BakingCompleted event) {
		ModelHandler.remapModels(event);
	}

	@SubscribeEvent
	public void onRegisterColorHandlers(RegisterColorHandlersEvent.Item event) {
		ColorHandler.setup(event);
	}

	@SubscribeEvent
	public void onAddPackFinders(AddPackFindersEvent event) {
		if(event.getPackType() == PackType.CLIENT_RESOURCES) {
			event.addRepositorySource(ResourceInjector.PackFinder.INSTANCE);
		}
	}

	public void onTagsUpdated(TagsUpdatedEvent event) {
		if(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			MaterialHandler.setClientTagsBound(true);
		}
	}

	public void onPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		MaterialHandler.setClientTagsBound(false);
	}
}
