package thelm.jaopca.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
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
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			RenderTypeLookup.setRenderLayer(block.asBlock(), RenderType.getTranslucent());
		}
		for(IMaterialFormFluid fluid : FluidFormType.getFluids()) {
			RenderTypeLookup.setRenderLayer(fluid.asFluid(), RenderType.getTranslucent());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			RenderTypeLookup.setRenderLayer(fluidBlock.asBlock(), RenderType.getTranslucent());
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

	public void onTextureStitchPre(TextureStitchEvent.Pre event) {

	}
}
