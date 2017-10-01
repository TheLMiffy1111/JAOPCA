package thelm.jaopca.modules;

import thelm.jaopca.api.ModuleBase;

public class ModuleRailcraft extends ModuleBase {

	@Override
	public String getName() {
		return "railcraft";
	}

	/*@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("industrialcraft");
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushed")) {
			for(ItemStack ore : OreDictionary.getOres("ore"+entry.getOreName())) {
				RailcraftCraftingManager.rockCrusher.
				createAndAddRecipe(ore, true, false).
				addOutput(Utils.getOreStack("crushed", entry, 2), 1F);
			}
		}
	}*/
}
