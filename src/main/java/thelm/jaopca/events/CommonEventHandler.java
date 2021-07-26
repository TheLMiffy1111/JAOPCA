package thelm.jaopca.events;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.event.RegistryEvent;
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
		ApiImpl.INSTANCE.init();
		event.enqueueWork(()->{
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
		});
	}

	@SubscribeEvent
	public void onRegister(RegistryEvent.Register event) {
		RegistryHandler.onRegister(event);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(()->{
			ModuleHandler.onCommonSetup(event);
		});
	}

	@SubscribeEvent
	public void onInterModEnqueue(InterModEnqueueEvent event) {
		ModuleHandler.onInterModEnqueue(event);
	}

	public void onDataPackDiscovery(PackRepository resourcePacks) {
		resourcePacks.addPackFinder(DataInjector.PackFinder.INSTANCE);
	}

	public void onReadRecipes(Map<ResourceLocation, JsonElement> recipeMap) {
		DataInjector.injectRecipes(recipeMap);
	}
}
