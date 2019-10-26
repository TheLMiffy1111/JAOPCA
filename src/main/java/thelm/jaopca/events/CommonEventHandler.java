package thelm.jaopca.events;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.block.Block;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.utils.ApiImpl;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ListMultimap<Class<?>, Consumer<IForgeRegistry>> REGISTRY_EVENT_HANDLERS = MultimapBuilder.hashKeys().arrayListValues().build();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void registerRegistryEventHandler(Class<?> type, Consumer<IForgeRegistry> handler) {
		REGISTRY_EVENT_HANDLERS.put(type, handler);
	}

	public void onConstruct() {
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
		registerRegistryEventHandler(Block.class, this::onBlockRegister);
		registerRegistryEventHandler(Item.class, this::onItemRegister);
		registerRegistryEventHandler(Fluid.class, this::onFluidRegister);
		DeferredWorkQueue.runLater(()->{
			DataCollector.collectData();
			ModuleHandler.findModules();
			ConfigHandler.setupMainConfig();
			MaterialHandler.findMaterials();
			ConfigHandler.setupMaterialConfigs();
			FormTypeHandler.setupGson();
			ConfigHandler.setupCustomFormConfig();
			ConfigHandler.setupModuleConfigsPre();
			FormHandler.collectForms();
			ModuleHandler.computeValidMaterials();
			FormHandler.computeValidMaterials();
			ConfigHandler.setupModuleConfigs();
		});
	}

	@SubscribeEvent
	public void onRegister(RegistryEvent.Register event) {
		IForgeRegistry<?> registry = event.getRegistry();
		for(Consumer<IForgeRegistry> listener : REGISTRY_EVENT_HANDLERS.get(registry.getRegistrySuperType())) {
			listener.accept(registry);
		}
	}

	public void onBlockRegister(IForgeRegistry<Block> registry) {
		BlockFormType.registerBlocks(registry);
		FluidFormType.registerBlocks(registry);
	}

	public void onItemRegister(IForgeRegistry<Item> registry) {
		BlockFormType.registerItems(registry);
		ItemFormType.registerItems(registry);
		FluidFormType.registerItems(registry);
	}

	public void onFluidRegister(IForgeRegistry<Fluid> registry) {
		FluidFormType.registerFluids(registry);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(()->{
			ModuleHandler.onCommonSetup(event);
		});
	}

	@SubscribeEvent
	public void onInterModEnqueue(InterModEnqueueEvent event) {
		ModuleHandler.onInterModEnqueue(event);
	}

	@SubscribeEvent
	public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();
		List<IFutureReloadListener> reloadListeners = ((SimpleReloadableResourceManager)server.getResourceManager()).reloadListeners;
		DataInjector instance = DataInjector.getNewInstance(server.getRecipeManager());
		reloadListeners.add(reloadListeners.indexOf(server.getRecipeManager())+1, new ReloadListener<Object>() {
			@Override
			protected Object prepare(IResourceManager resourceManager, IProfiler profiler) {
				return null;
			}
			@Override
			protected void apply(Object splashList, IResourceManager resourceManager, IProfiler profiler) {
				instance.injectRecipes(resourceManager);
			}
		});
		server.getResourcePacks().addPackFinder(DataInjector.PackFinder.INSTANCE);
	}
}
