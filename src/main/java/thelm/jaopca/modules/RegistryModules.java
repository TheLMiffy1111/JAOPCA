package thelm.jaopca.modules;

import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.JAOPCAApi;

public class RegistryModules {

	public static void preInit() {
		JAOPCAApi.registerModule(new ModuleDust());
		JAOPCAApi.registerModule(new ModuleNugget());
		JAOPCAApi.registerModule(new ModuleBlock());
		JAOPCAApi.registerModule(new ModuleMolten());
		JAOPCAApi.registerModule(new ModuleCoin());
		JAOPCAApi.registerModule(new ModuleGear());
		JAOPCAApi.registerModule(new ModulePlate());
		JAOPCAApi.registerModule(new ModuleStick());
		if(Loader.isModLoaded("mekanism")) {
			JAOPCAApi.registerModule(new ModuleMekanism());
		}
		if(Loader.isModLoaded("tconstruct")) {
			JAOPCAApi.registerModule(new ModuleTinkersConstruct());
		}
		if(Loader.isModLoaded("ic2")) {
			JAOPCAApi.registerModule(new ModuleIndustrialCraft());
		}
		if(Loader.isModLoaded("appliedenergistics2")) {
			JAOPCAApi.registerModule(new ModuleAppliedEnergistics());
		}
		if(Loader.isModLoaded("enderio")) {
			JAOPCAApi.registerModule(new ModuleEnderIO());
		}
		if(Loader.isModLoaded("thermalexpansion")) {
			JAOPCAApi.registerModule(new ModuleThermalExpansion());
		}
		if(Loader.isModLoaded("exnihiloadscensio")) {
			JAOPCAApi.registerModule(new ModuleExNihiloAdscensio());
		}
		if(Loader.isModLoaded("immersiveengineering")) {
			JAOPCAApi.registerModule(new ModuleImmersiveEngineering());
		}
		if(Loader.isModLoaded("railcraft")) {
			//should remove itself if industrialcraft is not loaded
			JAOPCAApi.registerModule(new ModuleRailcraft());
		}
		if(Loader.isModLoaded("embers")) {
			JAOPCAApi.registerModule(new ModuleEmbers());
		}
		if(Loader.isModLoaded("fp")) {
			JAOPCAApi.registerModule(new ModuleFuturePack());
		}
		if(Loader.isModLoaded("abyssalcraft")) {
			JAOPCAApi.registerModule(new ModuleAbyssalCraft());
		}
		if(Loader.isModLoaded("techreborn")) {
			JAOPCAApi.registerModule(new ModuleTechReborn());
		}
		if(Loader.isModLoaded("skyresources")) {
			JAOPCAApi.registerModule(new ModuleSkyResources());
		}
	}
}
