package thelm.jaopca.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
import thelm.jaopca.modules.ModuleHandler;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		Minecraft mc = Minecraft.getInstance();
		mc.getResourcePackRepository().addPackFinder(ResourceInjector.PackFinder.INSTANCE);
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
		event.enqueueWork(()->{
			ModuleHandler.onClientSetup(event);
		});
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			ItemBlockRenderTypes.setRenderLayer(block.asBlock(), RenderType.translucent());
		}
		for(IMaterialFormFluid fluid : FluidFormType.getFluids()) {
			ItemBlockRenderTypes.setRenderLayer(fluid.asFluid(), RenderType.translucent());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			ItemBlockRenderTypes.setRenderLayer(fluidBlock.asBlock(), RenderType.translucent());
		}
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

	public void onTextureStitchPre(TextureStitchEvent.Pre event) {

	}
}
