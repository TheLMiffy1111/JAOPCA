package thelm.jaopca.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.ingredients.IngredientTypes;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();
	private static final Logger LOGGER = LogManager.getLogger();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onConstruct(FMLConstructModEvent event) {
		NeoForge.EVENT_BUS.addListener(this::onAddReloadListener);
		ApiImpl.INSTANCE.init();
		DataInjector.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
		IngredientTypes.init();
		DataCollector.collectData();
		ModuleHandler.findModules();
		ConfigHandler.setupMainConfig();
		DataInjector.findDataModules();
		MaterialHandler.findMaterials();
		ConfigHandler.setupMaterialConfigs();
		ConfigHandler.setupCustomFormConfig();
		ConfigHandler.setupModuleConfigsPre();
		FormHandler.collectForms();
		ModuleHandler.computeValidMaterials();
		FormHandler.computeValidMaterials();
		ConfigHandler.setupModuleConfigs();
		FormTypeHandler.registerMaterialForms();
		ModuleHandler.onMaterialComputeComplete();
		RegistryHandler.registerRegistryEntry(Registries.CREATIVE_MODE_TAB, "tab",
				()->CreativeModeTab.builder().
				title(Component.translatable("itemGroup.jaopca")).
				icon(()->new ItemStack(Items.GLOWSTONE_DUST)).
				displayItems(FormTypeHandler::addToCreativeModeTab).
				build());
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ModuleHandler.onCommonSetup(event);
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		FormTypeHandler.onRegisterCapabilities(event);
	}

	@SubscribeEvent
	public void onInterModEnqueue(InterModEnqueueEvent event) {
		ModuleHandler.onInterModEnqueue(event);
	}

	@SubscribeEvent
	public void onAddPackFinders(AddPackFindersEvent event) {
		if(event.getPackType() == PackType.SERVER_DATA) {
			event.addRepositorySource(DataInjector.PackFinder.INSTANCE);
		}
	}

	public void onAddReloadListener(AddReloadListenerEvent event) {
		MiscHelper.INSTANCE.setTagManager(event.getServerResources().listeners().stream().
				filter(l->l instanceof TagManager).findFirst().map(l->(TagManager)l).
				orElseThrow(()->new IllegalStateException("Tag manager not found.")));
	}

	public void onReloadApply(Class<?> clazz, Object object) {
		DataInjector.reloadInject(clazz, object);
	}
}
