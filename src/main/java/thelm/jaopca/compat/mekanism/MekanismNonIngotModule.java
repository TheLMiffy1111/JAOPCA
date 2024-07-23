package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "mekanism")
public class MekanismNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Amber", "Amethyst", "Apatite", "Coal", "Diamond", "Emerald", "Lapis", "Malachite", "Peridot",
			"Quartz", "Redstone", "Ruby", "Sapphire", "Tanzanite", "Topaz"));

	static {
		if(Loader.isModLoaded("appliedenergistics2")) {
			Collections.addAll(BLACKLIST, "CertusQuartz", "ChargedCertusQuartz");
		}
		if(Loader.isModLoaded("mysticalagriculture")) {
			Collections.addAll(BLACKLIST, "Inferium", "Prosperity");
		}
	}

	@Override
	public String getName() {
		return "mekanism_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			helper.registerEnrichmentChamberRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_material", material.getName()),
					oreOredict, 1, materialOredict, material.getType().isCrystalline() ? 2 : 5);
		}
	}
}
