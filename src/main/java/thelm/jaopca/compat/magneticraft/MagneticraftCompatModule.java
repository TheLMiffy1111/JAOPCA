package thelm.jaopca.compat.magneticraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.Loader;
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

@JAOPCAModule(modDependencies = "magneticraft")
public class MagneticraftCompatModule implements IModule {

	private static final Set<String> BLOCK_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Copper", "Gold", "Iron", "Lead", "Tungsten"));
	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Cobalt", "Copper", "Gold", "Iron", "Lead", "Mithril", "Nickel", "Osmium",
			"Silver", "Steel", "Tin", "Tungsten", "Zinc"));
	private static final Set<String> HEAVY_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Copper", "Gold", "Iron", "Lead", "Steel", "Tungsten"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>();
	private static Set<String> configBlockToLightPlateBlacklist = new TreeSet<>();
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToHeavyPlateBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToLightPlateBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();

	static {
		if(Loader.isModLoaded("thermalfoundation")) {
			Collections.addAll(TO_PLATE_BLACKLIST, "Aluminium", "Aluminum", "Constantan", "Copper", "Electrum",
					"Enderium", "Gold", "Invar", "Iridium", "Iron", "Lead", "Lumium", "Mithril", "Nickel",
					"Platinum", "Signalum", "Steel", "Tin");
		}
	}

	@Override
	public String getName() {
		return "magneticraft_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToLightPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crushing table to light plate recipes added."),
				configBlockToLightPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have grinder to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toHeavyPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hydraulic press to heavy plate recipes added."),
				configToHeavyPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToLightPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hydraulic press to light plate recipes added."),
				configMaterialToLightPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hydraulic press to plate recipes added."),
				configToPlateBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MagneticraftHelper helper = MagneticraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !BLOCK_PLATE_BLACKLIST.contains(name) && !configBlockToLightPlateBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String lightPlateOredict = miscHelper.getOredictName("lightPlate", name);
				if(oredict.contains(blockOredict) && oredict.contains(lightPlateOredict)) {
					helper.registerCrushingTableRecipe(
							miscHelper.getRecipeKey("magneticraft.block_to_light_plate", name),
							blockOredict, lightPlateOredict, 5);
				}
			}
			if(type.isIngot() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("magneticraft.material_to_dust", name),
							materialOredict, dustOredict, 1, 50);
				}
			}
			if(type.isIngot() && !HEAVY_PLATE_BLACKLIST.contains(name) && !configToHeavyPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String heavyPlateOredict = miscHelper.getOredictName("heavyPlate", name);
				if(oredict.contains(heavyPlateOredict)) {
					helper.registerHydraulicPressRecipe(
							miscHelper.getRecipeKey("magneticraft.material_to_heavy_plate", name),
							materialOredict, 4, heavyPlateOredict, 1, 100, 2);
				}
			}
			if(type.isIngot() && !HEAVY_PLATE_BLACKLIST.contains(name) && !configMaterialToLightPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String lightPlateOredict = miscHelper.getOredictName("lightPlate", name);
				if(oredict.contains(lightPlateOredict)) {
					helper.registerHydraulicPressRecipe(
							miscHelper.getRecipeKey("magneticraft.material_to_light_plate", name),
							materialOredict, 1, lightPlateOredict, 1, 100, 1);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict)) {
					helper.registerHydraulicPressRecipe(
							miscHelper.getRecipeKey("magneticraft.material_to_plate", name),
							materialOredict, 1, plateOredict, 1, 80, 0);
				}
			}
		}
	}
}
