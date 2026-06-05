package thelm.jaopca.client.events;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
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
		ModuleHandler.onClientSetup(event);
	}

	@SubscribeEvent
	public void onRegisterClientReloadListeners(AddClientReloadListenersEvent event) {
		event.addListener(Identifier.parse("jaopca:localization"), new SimplePreparableReloadListener<>() {
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
	public void onRegisterColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
		ColorHandler.setupBlockTint(event);
	}

	@SubscribeEvent
	public void onRegisterColorHandlers(RegisterColorHandlersEvent.ItemTintSources event) {
		ColorHandler.setupItemTint(event);
	}

	@SubscribeEvent
	public void onRegisterFluidModels(RegisterFluidModelsEvent event) {
		ModelHandler.registerFluidModels(event);
	}

	@SubscribeEvent
	public void onModelModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		ModelHandler.remapItemModels(event);
	}

	@SubscribeEvent
	public void onAddPackFinders(AddPackFindersEvent event) {
		if(event.getPackType() == PackType.CLIENT_RESOURCES) {
			event.addRepositorySource(ResourceInjector.PackFinder.INSTANCE);
		}
	}

	public void onTagsUpdated(TagsUpdatedEvent.ClientPacketReceived event) {
		MaterialHandler.setClientTagsBound(true);
	}

	public void onPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		MaterialHandler.setClientTagsBound(false);
	}
}
