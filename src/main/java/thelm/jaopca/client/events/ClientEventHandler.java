package thelm.jaopca.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.blocks.BlockFormType;
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
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
		Minecraft mc = event.getMinecraftSupplier().get();
		mc.getResourcePackRepository().addPackFinder(ResourceInjector.PackFinder.INSTANCE);
		LocalizationRepoHandler.setup();
		((IReloadableResourceManager)mc.getResourceManager()).registerReloadListener(new ReloadListener<Object>() {
			@Override
			protected Object prepare(IResourceManager resourceManager, IProfiler profiler) {
				return null;
			}
			@Override
			protected void apply(Object splashList, IResourceManager resourceManager, IProfiler profiler) {
				LocalizationRepoHandler.reload();
			}
		});
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			RenderTypeLookup.setRenderLayer(block.toBlock(), RenderType.translucent());
		}
		for(IMaterialFormFluid fluid : FluidFormType.getFluids()) {
			RenderTypeLookup.setRenderLayer(fluid.toFluid(), RenderType.translucent());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			RenderTypeLookup.setRenderLayer(fluidBlock.toBlock(), RenderType.translucent());
		}
		ModuleHandler.onClientSetup(event);
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

	public void onTagsUpdated(TagsUpdatedEvent event) {
		if(Minecraft.getInstance().isSameThread()) {
			MaterialHandler.setClientTagsBound(true);
		}
	}

	public void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		MaterialHandler.setClientTagsBound(false);
	}
}
