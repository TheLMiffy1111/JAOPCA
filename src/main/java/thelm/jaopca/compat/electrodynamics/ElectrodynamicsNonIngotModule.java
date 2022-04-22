package thelm.jaopca.compat.electrodynamics;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "electrodynamics")
public class ElectrodynamicsNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "lapis", "niter", "potassiumchloride", "redstone", "salt", "sulfur"));

	@Override
	public String getName() {
		return "electrodynamics_non_ingot";
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
		ElectrodynamicsHelper helper = ElectrodynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			boolean isCrystal = material.getType() != MaterialType.DUST;

			helper.registerMineralGrinderRecipe(
					new ResourceLocation("jaopca", "electrodynamics.ore_to_material_grinder."+material.getName()),
					oreLocation, 1, materialLocation, isCrystal ? 3 : 2, isCrystal ? 1 : 0.3);
			if(!isCrystal) {
				helper.registerMineralCrusherRecipe(
						new ResourceLocation("jaopca", "electrodynamics.ore_to_material_crusher."+material.getName()),
						oreLocation, 1, materialLocation, 4, 0.3);
			}
		}
	}
}
