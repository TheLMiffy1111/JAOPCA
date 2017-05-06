package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fluids.Fluid;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleTinkersConstruct extends ModuleAbstract {

	@Override
	public String getName() {
		return "tconstruct";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList();
	}

	@Override
	public void registerRecipes() {
		//Use TConstruct's internal method because it adds everything for us
		//Might change
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("molten")) {
			TinkerSmeltery.registerOredictMeltingCasting(JAOPCAApi.FLUIDS_TABLE.get("molten", entry.getOreName()), entry.getOreName());
		}
	}
	
	public static void addMeltingRecipe(String oreName, Fluid fluid, int amount) {
		TinkerRegistry.registerMelting(oreName, fluid, amount);
	}
}
