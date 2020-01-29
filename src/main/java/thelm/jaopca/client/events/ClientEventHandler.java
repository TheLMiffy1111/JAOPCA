package thelm.jaopca.client.events;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
import thelm.jaopca.fluids.FluidFormType;
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
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			RenderTypeLookup.setRenderLayer(block.asBlock(), RenderType.translucent());
		}
		for(IMaterialFormFluid fluid : FluidFormType.getFluids()) {
			RenderTypeLookup.setRenderLayer(fluid.asFluid(), RenderType.translucent());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			RenderTypeLookup.setRenderLayer(fluidBlock.asBlock(), RenderType.translucent());
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

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {

	}
}
