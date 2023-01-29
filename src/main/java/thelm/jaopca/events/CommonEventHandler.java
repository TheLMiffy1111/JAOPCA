package thelm.jaopca.events;

import java.io.File;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.oredict.OredictHandler;
import thelm.jaopca.recipes.RecipeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.wrapup.event.InitializationWrapUpEvent;
import thelm.wrapup.event.PostInitializationWrapUpEvent;
import thelm.wrapup.event.PreInitializationWrapUpEvent;
import thelm.wrapup.event.RegistryWrapUpEvent;

public class CommonEventHandler {

	protected ASMDataTable asmDataTable;
	protected File modConfigDir;

	public void onPreInit(FMLPreInitializationEvent event) {
		asmDataTable = event.getAsmData();
		modConfigDir = event.getModConfigurationDirectory();
	}

	@SubscribeEvent
	public void onPreInitWrapUp2(PreInitializationWrapUpEvent.Event2 event) {
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
		ModuleHandler.findModules(asmDataTable);
		ConfigHandler.setupMainConfig(modConfigDir);
		OredictHandler.findOredictModules(asmDataTable);
	}

	@SubscribeEvent
	public void onRegistryWrapUp2(RegistryWrapUpEvent.Event2 event) {
		OredictHandler.register();
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
		RecipeHandler.registerEarlyRecipes();
	}

	public void onInit(FMLInitializationEvent event) {

	}

	@SubscribeEvent
	public void onInitWrapUp2(InitializationWrapUpEvent.Event2 event) {
		ModuleHandler.onInit(event.event);
		RecipeHandler.registerRecipes();
	}

	@SubscribeEvent
	public void onPostInitWrapUp2(PostInitializationWrapUpEvent.Event2 event) {
		ModuleHandler.onPostInit(event.event);
		RecipeHandler.registerLateRecipes();
	}

	@SubscribeEvent
	public void onOreRegister(OreDictionary.OreRegisterEvent event) {
		OredictHandler.onOreRegister(event);
	}

	@SubscribeEvent
	public void onMissingMappings(RegistryEvent.MissingMappings event) {
		RegistryHandler.onMissingMappings(event);
	}
}
