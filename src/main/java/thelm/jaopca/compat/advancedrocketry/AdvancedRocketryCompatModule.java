package thelm.jaopca.compat.advancedrocketry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "advancedrocketry@[1.12.2-2,)")
public class AdvancedRocketryCompatModule implements IModule {

	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Copper", "Gold", "Iridium", "Iron", "Steel", "Tin", "Titanium",
			"TitaniumAluminide", "TitaniumIridium"));
	private static Set<String> configToPlateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "advancedrocketry_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate press to plate recipes added."),
				configToPlateBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AdvancedRocketryHelper helper = AdvancedRocketryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(blockOredict) && oredict.contains(plateOredict)) {
					helper.registerSmallPlatePressRecipe(
							miscHelper.getRecipeKey("advancedrocketry.block_to_plate", material.getName()),
							blockOredict, plateOredict, material.isSmallStorageBlock() ? 2 : 4);
				}
			}
		}
	}
}
