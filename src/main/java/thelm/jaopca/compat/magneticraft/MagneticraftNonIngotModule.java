package thelm.jaopca.compat.magneticraft;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "Magneticraft")
public class MagneticraftNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Coal", "Diamond", "Emerald", "Lapis", "NetherQuartz", "Quartz", "Redstone", "Salt", "Sulfur"));

	@Override
	public String getName() {
		return "magneticraft_non_ingot";
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
		MagneticraftHelper helper = MagneticraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			if(material.getType().isDust()) {
				helper.registerCrusherRecipe(
						miscHelper.getRecipeKey("magneticraft.ore_to_material", material.getName()),
						oreOredict, materialOredict, 2, materialOredict, 1, 0.4F);
			}
			else {
				helper.registerCrusherRecipe(
						miscHelper.getRecipeKey("magneticraft.ore_to_material", material.getName()),
						oreOredict, materialOredict, 1, materialOredict, 1, 0.3F, materialOredict, 2, 0.01F);
			}
		}
	}
}
