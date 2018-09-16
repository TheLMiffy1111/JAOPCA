package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import charcoalPit.crafting.OreSmeltingRecipes;
import charcoalPit.crafting.OreSmeltingRecipes.AlloyRecipe;
import net.minecraftforge.common.config.Configuration;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleCharcoalPit extends ModuleBase {

	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Copper", "Zinc", "Silver", "Tin", "Gold", "Lead", "Bismuth", "Aluminium", "Titanium", "Iron", "Nickel", "Platinum"
			);
	
	public static final HashMap<IOreEntry, Boolean> ADVANCED_ORE = Maps.<IOreEntry, Boolean>newHashMap();
	
	@Override
	public String getName() {
		return "charcoalpit";
	}

	@Override
	public List<String> getOreBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			ADVANCED_ORE.put(entry, config.get(Utils.to_under_score(entry.getOreName()), "charcoalPitAdvanced", false, "Does this ore require the bloomery. (Charcoal Pit)").setRequiresMcRestart(true).getBoolean());
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			OreSmeltingRecipes.addAlloyRecipe(new AlloyRecipe("ingot"+entry.getOreName(), 1, ADVANCED_ORE.get(entry), true, entry.getOreName()));
		}
	}
}
