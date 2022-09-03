package thelm.jaopca.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.localization.LocalizationRepoHandler;
import thelm.jaopca.modules.ModuleHandler;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		Minecraft mc = Minecraft.getInstance();
		LocalizationRepoHandler.setup();
		((ReloadableResourceManager)mc.getResourceManager()).registerReloadListener(new SimplePreparableReloadListener() {
			@Override
			protected Object prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
				return null;
			}
			@Override
			protected void apply(Object splashList, ResourceManager resourceManager, ProfilerFiller profiler) {
				LocalizationRepoHandler.reload();
			}
		});
		for(IMaterialFormFluid fluid : FluidFormType.getFluids()) {
			ItemBlockRenderTypes.setRenderLayer(fluid.asFluid(), RenderType.translucent());
		}
		ModuleHandler.onClientSetup(event);
	}

	@SubscribeEvent
	public void onModelRegisterAdditional(ModelEvent.RegisterAdditional event) {
		ModelHandler.registerModels(event);
	}

	@SubscribeEvent
	public void onModelBake(ModelEvent.BakingCompleted event) {
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
}
