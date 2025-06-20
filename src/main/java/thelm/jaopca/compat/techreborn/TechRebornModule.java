package thelm.jaopca.compat.techreborn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "techreborn")
public class TechRebornModule implements IModule {

	static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"bauxite", "certus_quartz", "cinnabar", "coal", "copper", "diamond", "emerald", "galena", "gold", "iron",
			"lapis", "lead", "netherite", "netherite_scrap", "peridot", "pyrite", "quartz", "redstone", "ruby",
			"sapphire", "sheldonite", "silver", "sodalite", "sphalerite", "sulfur", "tin"));

	@Override
	public String getName() {
		return "techreborn";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("techreborn.ore_to_raw_material_grinder", material.getName()),
					oreLocation, 1, rawMaterialLocation, 2, 2, 270);
		}
	}
}
