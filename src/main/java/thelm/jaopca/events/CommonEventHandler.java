package thelm.jaopca.events;

import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.tags.TagManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
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
import thelm.jaopca.ingredients.IngredientSerializers;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
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
		MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
		IngredientSerializers.init();
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
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ModuleHandler.onCommonSetup(event);
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
		Optional<PreparableReloadListener> tagManager = event.getServerResources().listeners().stream().filter(l -> l instanceof TagManager).findFirst();
		if (tagManager.isEmpty()) {
			throw new IllegalStateException("Tag manager not found.");
		}
		MiscHelper.INSTANCE.setTagManager((TagManager)tagManager.get());
	}

	public void onReadRecipes(Map<ResourceLocation, JsonElement> recipeMap) {
		DataInjector.injectRecipes(recipeMap);
	}
}
