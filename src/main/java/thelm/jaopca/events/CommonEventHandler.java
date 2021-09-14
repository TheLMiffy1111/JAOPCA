package thelm.jaopca.events;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();
	private static final Logger LOGGER = LogManager.getLogger();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
		
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
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
		BlockFormType.registerEntries();
		ItemFormType.registerEntries();
		FluidFormType.registerEntries();
		ModuleHandler.onMaterialComputeComplete();
	}

	@SubscribeEvent
	public void onRegister(RegistryEvent.Register event) {
		RegistryHandler.onRegister(event);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ModuleHandler.onCommonSetup(event);
	}

	@SubscribeEvent
	public void onInterModEnqueue(InterModEnqueueEvent event) {
		ModuleHandler.onInterModEnqueue(event);
	}

	public void onDataPackDiscovery(ResourcePackList resourcePacks) {
		resourcePacks.addPackFinder(DataInjector.PackFinder.INSTANCE);
	}

	public void onAddReloadListener(AddReloadListenerEvent event) {
		DataPackRegistries registries = event.getDataPackRegistries();
		List<IFutureReloadListener> reloadListeners = ((SimpleReloadableResourceManager)registries.getResourceManager()).reloadListeners;
		DataInjector instance = DataInjector.getNewInstance(registries.getRecipeManager());
		reloadListeners.add(reloadListeners.indexOf(registries.getRecipeManager())+1, instance);
	}
}
