package thelm.jaopca.events;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.utils.ApiImpl;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();
	private static final Logger LOGGER = LogManager.getLogger();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		//FluidFormType.init();
		DataCollector.collectData();
		ModuleHandler.findModules();
		File configDir = new File(FMLPaths.CONFIGDIR.get().toFile(), JAOPCA.MOD_ID);
		ConfigHandler.setupMainConfig(configDir);
		MaterialHandler.findMaterials();
		ConfigHandler.setupMaterialConfigs();
		ConfigHandler.setupModuleConfigsPre();
		FormHandler.collectForms();
		ModuleHandler.computeValidMaterials();
		FormHandler.computeValidMaterials();
		ConfigHandler.setupModuleConfigs();
	}

	@SubscribeEvent
	public void onBlockRegister(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		BlockFormType.registerBlocks(registry);
	}

	@SubscribeEvent
	public void onItemRegister(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		BlockFormType.registerItemBlocks(registry);
		ItemFormType.registerItems(registry);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ModuleHandler.onCommonSetup(event);
	}

	@SubscribeEvent
	public void onInterModEnqueue(InterModEnqueueEvent event) {
		ModuleHandler.onInterModEnqueue(event);
	}

	@SubscribeEvent
	public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();
		List<IResourceManagerReloadListener> reloadListeners;
		try {
			Field reloadListenersField = Arrays.stream(SimpleReloadableResourceManager.class.getDeclaredFields()).filter(field->field.getType() == List.class).findFirst().get();
			reloadListenersField.setAccessible(true);
			reloadListeners = (List<IResourceManagerReloadListener>)reloadListenersField.get(server.getResourceManager());
		}
		catch(Exception e) {
			LOGGER.warn("Unable to obtain listener list.", e);
			return;
		}
		DataInjector instance = DataInjector.getNewInstance(server.getNetworkTagManager(), server.getRecipeManager(), server.getAdvancementManager());
		reloadListeners.add(reloadListeners.indexOf(server.getNetworkTagManager())+1, instance::injectTags);
		reloadListeners.add(reloadListeners.indexOf(server.getRecipeManager())+1, instance::injectRecipes);
		reloadListeners.add(reloadListeners.indexOf(server.getAdvancementManager())+1, instance::injectAdvancements);
	}
}
