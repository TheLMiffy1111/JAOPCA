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
public class IntegratedDynamicsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "ardite", "cobalt", "copper", "gold", "iron", "lead", "mithril", "netherite",
			"netherite_scrap", "nickel", "osmium", "platinum", "silver", "tin", "uranium", "zinc"));

	@Override
	public String getName() {
		return "integrateddynamics";
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
		JAOPCAApi api = ApiImpl.INSTANCE;
		IntegratedDynamicsHelper helper = IntegratedDynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			helper.registerSqueezerRecipe(
					new ResourceLocation("jaopca", "integrateddynamics.ore_to_raw_material."+material.getName()),
					oreLocation, new Object[] {
							rawMaterialLocation, 4, 1F,
							rawMaterialLocation, 2, 0.5F,
							rawMaterialLocation, 2, 0.5F,
					});
			helper.registerMechanicalSqueezerRecipe(
					new ResourceLocation("jaopca", "integrateddynamics.ore_to_raw_material_mechanical."+material.getName()),
					oreLocation, new Object[] {
							rawMaterialLocation, 8, 1F,
							rawMaterialLocation, 2, 0.5F,
							rawMaterialLocation, 2, 0.5F,
					}, 40);
		}
	}
}
