package thelm.jaopca.compat.voluminousenergy;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "voluminousenergy@[1.19.3-0.4.0.0,)")
public class VoluminousEnergyCrystalModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"bauxite", "cinnabar", "coal", "diamond", "emerald", "galena", "lapis", "quartz", "redstone",
			"rutile", "saltpeter", "sulfur"));

	@Override
	public String getName() {
		return "voluminousenergy_crystal";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		VoluminousEnergyHelper helper = VoluminousEnergyHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "voluminousenergy.ore_to_material."+material.getName()),
					oreLocation, 1, materialLocation, 1, 200, 1, 4);
		}
	}
}
