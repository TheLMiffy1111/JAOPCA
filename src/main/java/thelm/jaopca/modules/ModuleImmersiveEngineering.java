package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import blusunrize.immersiveengineering.common.IERecipes;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleImmersiveEngineering extends ModuleAbstract {

	@Override
	public String getName() {
		return "immersiveengineering";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList();
	}

	@Override
	public void registerRecipes() {
		IERecipes.oreOutputSecondaries.replace("Iridium", new Object[] {"dustPlatinum", Float.valueOf(0.1F)});

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dust")) {
			if(!entry.getOreName().equals(entry.getExtra())) {
				IERecipes.oreOutputSecondaries.putIfAbsent(entry.getOreName(), new Object[] {"dust"+entry.getExtra(), Float.valueOf(0.1F)});
			}
		}
	}
}
