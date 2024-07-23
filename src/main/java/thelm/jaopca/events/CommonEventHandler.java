package thelm.jaopca.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
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

	@SubscribeEvent
	public void onConstruct(FMLConstructModEvent event) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onAddReloadListener);
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
		DataCollector.collectData();
		ModuleHandler.findModules();
		ConfigHandler.setupMainConfig();
		DataInjector.findDataModules();
		MaterialHandler.findMaterials();
		ConfigHandler.setupMaterialConfigs();
		FormTypeHandler.setupGson();
		ConfigHandler.setupCustomFormConfig();
		ConfigHandler.setupModuleConfigsPre();
		FormHandler.collectForms();
		ModuleHandler.computeValidMaterials();
		FormHandler.computeValidMaterials();
		ConfigHandler.setupModuleConfigs();
		FormTypeHandler.registerMaterialForms();
		ModuleHandler.onMaterialComputeComplete();
	}

	@SuppressWarnings("rawtypes")
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
		event.addListener(DataInjector.getNewInstance(registries.getRecipeManager()));
	}
}
