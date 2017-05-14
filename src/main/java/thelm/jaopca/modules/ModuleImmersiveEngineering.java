package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import blusunrize.immersiveengineering.common.IERecipes;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;

public class ModuleImmersiveEngineering implements IModule {

	@Override
	public String getName() {
		return "immersiveengineering";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Iron", "Gold", "Copper", "Lead", "Silver", "Nickel", "Platinum", "Tungsten", "Uranium", "Yellorium", "Plutonium", "Osmium", "Iridium", "FzDarkIron"
				);
	}

	@Override
	public void registerRecipes() {
		IERecipes.oreOutputSecondaries.replace("Iridium", new Object[] {"dustPlatinum", Float.valueOf(0.1F)});

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(!entry.getModuleBlacklist().contains(getName())) {
				if(!entry.getOreName().equals(entry.getExtra())) {
					IERecipes.oreOutputSecondaries.put(entry.getOreName(), new Object[] {"dust"+entry.getExtra(), Float.valueOf(0.1F)});
				}
			}
		}
	}
}
