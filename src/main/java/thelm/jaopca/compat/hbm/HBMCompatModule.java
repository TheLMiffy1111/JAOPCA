package thelm.jaopca.compat.hbm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.hbm.inventory.fluid.Fluids;

import cpw.mods.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "hbm@[1.0.27_X4515,)")
public class HBMCompatModule implements IModule {

	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"AdvancedAlloy", "Aluminium", "Aluminum", "CMBSteel", "Copper", "Gold", "Iron", "Lead",
			"NaturalAluminum", "Saturnite", "Schrabidium", "Steel", "Titanium"));
	private static final Set<String> CRYSTALLIZER_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Diamond", "Emerald", "Lapis"));
	private static final Set<String> PRESS_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Coal", "Diamond", "Emerald", "Lapis", "Lignite", "NetherQuartz", "Quartz"));
	private static Set<String> configAnvilToPlateBlacklist = new TreeSet<>();
	private static Set<String> configPressToPlateBlacklist = new TreeSet<>();
	private static Set<String> configCrystallizerToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configPressToCrystalBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "hbm_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.anvilToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have anvil to plate recipes added."),
				configAnvilToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.pressToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to plate recipes added."),
				configPressToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.crystallizerToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystallizer to material recipes added."),
				configCrystallizerToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.pressToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to material recipes added."),
				configPressToCrystalBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		HBMHelper helper = HBMHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configAnvilToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict)) {
					helper.registerAnvilConstructionRecipe(
							miscHelper.getRecipeKey("hbm.material_to_plate_anvil", name),
							new Object[] {
									materialOredict, 1,
							}, new Object[] {
									plateOredict, 1, 1F,
							}, 3);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configPressToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict)) {
					helper.registerPressRecipe(
							miscHelper.getRecipeKey("hbm.material_to_plate_press", name),
							materialOredict, plateOredict, 1, "plate");
				}
			}
			if(type.isCrystalline() && !CRYSTALLIZER_TO_CRYSTAL_BLACKLIST.contains(name) && !configCrystallizerToCrystalBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(oredict.contains(dustOredict)) {
					helper.registerCrystallizerRecipe(
							miscHelper.getRecipeKey("hbm.dust_to_material_crystallizer", name),
							dustOredict, Fluids.ACID, 500, materialOredict, 1, 100);
				}
			}
			if(type.isCrystalline() && !PRESS_TO_CRYSTAL_BLACKLIST.contains(name) && !configPressToCrystalBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(oredict.contains(dustOredict)) {
					helper.registerPressRecipe(
							miscHelper.getRecipeKey("hbm.dust_to_material_press", name),
							dustOredict, materialOredict, 1, "flat");
				}
			}
		}
	}
}
