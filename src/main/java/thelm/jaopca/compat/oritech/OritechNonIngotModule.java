package thelm.jaopca.compat.oritech;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "oritech@[0.15.3,)")
public class OritechNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "lapis", "quartz", "redstone"));

	static {
		if(ModList.get().isLoaded("techreborn")) {
			Collections.addAll(BLACKLIST, "bauxite", "cinnabar", "galena", "peridot", "pyrite", "ruby",
					"sapphire", "sheldonite", "sodalite", "sphalerite");
		}
	}

	@Override
	public String getName() {
		return "oritech_non_ingot";
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
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		OritechHelper helper = OritechHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			Identifier oreLocation = miscHelper.getTagLocation("ores", material.getName());
			Identifier materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			boolean isCrystal = material.getType() != MaterialType.DUST;
			helper.registerPulverizerRecipe(
					miscHelper.getRecipeKey("oritech.ore_to_material_pulverizer", material.getName()),
					oreLocation, materialLocation, isCrystal ? 1 : 3, 200);
			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("oritech.ore_to_material_grinder", material.getName()),
					oreLocation, materialLocation, isCrystal ? 2 : 6, 140);
		}
	}
}
