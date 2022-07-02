package thelm.jaopca.compat.integrateddynamics;

import java.util.EnumSet;
import java.util.Set;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "integrateddynamics")
public class IntegratedDynamicsNonIngotModule implements IModule {

	@Override
	public String getName() {
		return "integrateddynamics_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return IntegratedDynamicsModule.BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IntegratedDynamicsHelper helper = IntegratedDynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			if(material.getType().isCrystalline()) {
				helper.registerSqueezerRecipe(
						miscHelper.getRecipeKey("integrateddynamics.ore_to_material", material.getName()),
						oreOredict, new Object[] {
								materialOredict, 1, 1F,
								materialOredict, 1, 0.75F,
						});
				helper.registerMechanicalSqueezerRecipe(
						miscHelper.getRecipeKey("integrateddynamics.ore_to_material_mechanical", material.getName()),
						oreOredict, new Object[] {
								materialOredict, 2, 1F,
								materialOredict, 1, 0.5F,
						}, 40);
			}
			else {
				helper.registerSqueezerRecipe(
						miscHelper.getRecipeKey("integrateddynamics.ore_to_material", material.getName()),
						oreOredict, new Object[] {
								materialOredict, 6, 1F,
								materialOredict, 2, 0.5F,
								materialOredict, 2, 0.5F,
						});
				helper.registerMechanicalSqueezerRecipe(
						miscHelper.getRecipeKey("integrateddynamics.ore_to_material_mechanical", material.getName()),
						oreOredict, new Object[] {
								materialOredict, 10, 1F,
								materialOredict, 2, 0.5F,
						}, 40);
			}
		}
	}
}
