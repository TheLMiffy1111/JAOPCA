package thelm.jaopca.compat.hbm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.hbm.forgefluid.ModForgeFluids;

import net.minecraftforge.fluids.Fluid;
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

@JAOPCAModule(modDependencies = "hbm", classDependencies = "com.hbm.main.MainRegistry")
public class HBMCompatModule implements IModule {

	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"AdvancedAlloy", "Aluminium", "Aluminum", "CMBSteel", "Copper", "Gold", "Iron", "Lead", "Saturnite",
			"Schrabidium", "Steel", "Titanium"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Diamond", "Emerald", "Lapis"));
	private static Set<String> configAnvilToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();

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
						helper.configMaterialPredicate(), "The materials that should not have anvil to plate recipes added. (Why are press recipes hardcoded)"),
				configAnvilToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystallizing to material recipes added."),
				configToCrystalBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		HBMHelper helper = HBMHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Fluid acid = ModForgeFluids.acid;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configAnvilToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), name);
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
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(oredict.contains(dustOredict)) {
					helper.registerCrystallizerRecipe(
							miscHelper.getRecipeKey("hbm.dust_to_material", name),
							dustOredict, acid, 500, materialOredict, 1);
				}
			}
		}
	}
}
