package thelm.jaopca.compat.integrateddynamics;

import java.util.EnumSet;
import java.util.List;
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

@JAOPCAModule(modDependencies = "integrateddynamics")
public class IntegratedDynamicsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
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
		IntegratedDynamicsHelper helper = IntegratedDynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			helper.registerSqueezerRecipe(
					miscHelper.getRecipeKey("integrateddynamics.ore_to_raw_material", material.getName()),
					oreLocation, new Object[] {
							rawMaterialLocation, 2, 1F,
							rawMaterialLocation, 1, 0.75F,
					});
			helper.registerMechanicalSqueezerRecipe(
					miscHelper.getRecipeKey("integrateddynamics.ore_to_raw_material_mechanical", material.getName()),
					oreLocation, new Object[] {
							rawMaterialLocation, 3, 1F,
							rawMaterialLocation, 2, 0.5F,
							rawMaterialLocation, 2, 0.5F,
					}, 40);
		}
	}
}
