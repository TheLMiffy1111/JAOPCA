package thelm.jaopca.compat.rotarycraft;

import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.RotaryCraft.Auxiliary.CustomExtractLoader;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.ExtractorModOres;
import Reika.RotaryCraft.ModInterface.ItemCustomModOre;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

/**
 * For easier support.
 */
@JAOPCAOredictModule(modDependencies = "RotaryCraft")
public class RotaryCraftOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "rotarycraft";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		for(ReikaOreHelper ore : ReikaOreHelper.values()) {
			for(String oreOredict : ore.getOreDictNames()) {
				if(oreOredict.startsWith("ore")) {
					api.registerOredict(oreOredict.replaceFirst("ore", "RotaryCraft:flakes"), ItemStacks.getFlake(ore));
				}
			}
		}
		for(ModOreList ore : ModOreList.values()) {
			for(String oreOredict : ore.getOreDictNames()) {
				if(oreOredict.startsWith("ore")) {
					api.registerOredict(oreOredict.replaceFirst("ore", "RotaryCraft:flakes"), ExtractorModOres.getFlakeProduct(ore));
				}
			}
		}
		for(CustomExtractLoader.CustomExtractEntry ore : CustomExtractLoader.instance.getEntries()) {
			for(String oreOredict : ore.getOreDictNames()) {
				if(oreOredict.startsWith("ore")) {
					api.registerOredict(oreOredict.replaceFirst("ore", "RotaryCraft:dust"), ItemCustomModOre.getItem(ore.ordinal, ExtractorModOres.ExtractorStage.DUST));
					api.registerOredict(oreOredict.replaceFirst("ore", "RotaryCraft:slurry"), ItemCustomModOre.getItem(ore.ordinal, ExtractorModOres.ExtractorStage.SLURRY));
					api.registerOredict(oreOredict.replaceFirst("ore", "RotaryCraft:solution"), ItemCustomModOre.getItem(ore.ordinal, ExtractorModOres.ExtractorStage.SOLUTION));
					api.registerOredict(oreOredict.replaceFirst("ore", "RotaryCraft:flakes"), ItemCustomModOre.getItem(ore.ordinal, ExtractorModOres.ExtractorStage.FLAKES));
				}
			}
		}
	}
}
