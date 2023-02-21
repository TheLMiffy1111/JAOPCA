package thelm.jaopca.events;

import java.io.File;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
import thelm.jaopca.utils.ApiImpl;

public class CommonEventHandler {

	protected ASMDataTable asmDataTable;
	protected File modConfigDir;

	public void onPreInit(FMLPreInitializationEvent event) {
		asmDataTable = event.getAsmData();
		modConfigDir = event.getModConfigurationDirectory();
		ApiImpl.INSTANCE.init();
		BlockFormType.init();
		ItemFormType.init();
		FluidFormType.init();
		ModuleHandler.findModules(asmDataTable);
		ConfigHandler.setupMainConfig(modConfigDir);
		OredictHandler.findOredictModules(asmDataTable);
		RecipeHandler.registerEarlyRecipes();
	}

	public void onInit(FMLInitializationEvent event) {
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
		ModuleHandler.onInit(event);
		RecipeHandler.registerRecipes();
	}

	public void onPostInit(FMLPostInitializationEvent event) {
		ModuleHandler.onPostInit(event);
		RecipeHandler.registerLateRecipes();
	}

	public void onLoadComplete(FMLLoadCompleteEvent event) {
		ModuleHandler.onLoadComplete(event);
		RecipeHandler.registerFinalRecipes();
	}

	@SubscribeEvent
	public void onOreRegister(OreDictionary.OreRegisterEvent event) {
		OredictHandler.onOreRegister(event);
	}
}
