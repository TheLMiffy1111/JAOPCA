package thelm.jaopca.compat.integrateddynamics;

import java.util.Arrays;
import java.util.EnumSet;
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

@JAOPCAModule(modDependencies = "integrateddynamics")
public class IntegratedDynamicsNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "lapis", "quartz", "redstone"));

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
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IntegratedDynamicsHelper helper = IntegratedDynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			if(material.getType() != MaterialType.DUST) {
				helper.registerSqueezerRecipe(
						new ResourceLocation("jaopca", "integrateddynamics.ore_to_material."+material.getName()),
						oreLocation, new Object[] {
								materialLocation, 1, 1F,
								materialLocation, 1, 0.75F,
						});
				helper.registerMechanicalSqueezerRecipe(
						new ResourceLocation("jaopca", "integrateddynamics.ore_to_material_mechanical."+material.getName()),
						oreLocation, new Object[] {
								materialLocation, 2, 1F,
								materialLocation, 1, 0.5F,
						}, 40);
			}
			else {
				helper.registerSqueezerRecipe(
						new ResourceLocation("jaopca", "integrateddynamics.ore_to_material."+material.getName()),
						oreLocation, new Object[] {
								materialLocation, 6, 1F,
								materialLocation, 2, 0.5F,
								materialLocation, 2, 0.5F,
						});
				helper.registerMechanicalSqueezerRecipe(
						new ResourceLocation("jaopca", "integrateddynamics.ore_to_material_mechanical."+material.getName()),
						oreLocation, new Object[] {
								materialLocation, 10, 1F,
								materialLocation, 2, 0.5F,
						}, 40);
			}
		}
	}
}
