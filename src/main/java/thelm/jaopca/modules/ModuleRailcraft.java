package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleRailcraft extends ModuleBase {

	@Override
	public String getName() {
		return "railcraft";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("industrialcraft");
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushed")) {
			for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
				RailcraftCraftingManager.rockCrusher.
				createAndAddRecipe(ore, true, false).
				addOutput(Utils.getOreStack("crushed", entry, 2), 1F);
			}
		}
	}
}
