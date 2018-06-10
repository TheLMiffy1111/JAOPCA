package thelm.jaopca.modules;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static thelm.jaopca.api.JAOPCAApi.registerModule;

public class RegistryModules {

	public static void preInit() {
		registerModule(new ModuleDust());
		registerModule(new ModuleNugget());
		registerModule(new ModuleBlock());
		registerModule(new ModuleMolten());
		registerModule(new ModuleCoin());
		registerModule(new ModuleGear());
		registerModule(new ModulePlate());
		registerModule(new ModuleStick());
		registerModule(new ModuleTinyDust());
		registerModule(new ModuleSmallDust());
		registerModule(new ModuleDensePlate());
		if(isModLoaded("mekanism")) {
			registerModule(new ModuleMekanism());
		}
		if(isModLoaded("tconstruct")) {
			registerModule(new ModuleTinkersConstruct());
		}
		if(isModLoaded("ic2")) {
			registerModule(new ModuleIndustrialCraft());
		}
		if(isModLoaded("appliedenergistics2")) {
			registerModule(new ModuleAppliedEnergistics());
		}
		if(isModLoaded("thermalexpansion")) {
			registerModule(new ModuleThermalExpansion());
		}
		if(isModLoaded("exnihiloadscensio")) {
			registerModule(new ModuleExNihiloAdscensio());
		}
		if(isModLoaded("immersiveengineering")) {
			registerModule(new ModuleImmersiveEngineering());
		}
		if(isModLoaded("railcraft")) {
			//should remove itself if industrialcraft is not loaded
			registerModule(new ModuleRailcraft());
		}
		if(isModLoaded("embers")) {
			registerModule(new ModuleEmbers());
		}
		if(isModLoaded("fp")) {
			registerModule(new ModuleFuturePack());
		}
		if(isModLoaded("abyssalcraft")) {
			registerModule(new ModuleAbyssalCraft());
		}
		if(isModLoaded("techreborn")) {
			registerModule(new ModuleTechReborn());
		}
		if(isModLoaded("skyresources")) {
			registerModule(new ModuleSkyResources());
		}
		if(isModLoaded("bcoreprocessing")) {
			registerModule(new ModuleBuildcraftOreProcessing());
		}
		if(isModLoaded("advancedrocketry")) {
			registerModule(new ModuleAdvancedRocketry());
		}
	}
}
