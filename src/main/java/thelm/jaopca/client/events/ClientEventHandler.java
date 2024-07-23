package thelm.jaopca.client.events;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
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
		NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);
		NeoForge.EVENT_BUS.addListener(this::onPlayerLoggingOut);
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
	public void onModelModifyBakingResult(ModelEvent.ModifyBakingResult event) {
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
