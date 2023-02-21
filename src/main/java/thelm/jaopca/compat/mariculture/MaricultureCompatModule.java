package thelm.jaopca.compat.mariculture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import mariculture.core.lib.MetalRates;
import net.minecraftforge.fluids.FluidRegistry;
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

@JAOPCAModule(modDependencies = "Mariculture")
public class MaricultureCompatModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Bronze", "Cobalt", "Copper", "Electrum", "Gold", "Iron", "Lead",
			"Magnesium", "NaturalAluminum", "Nickel", "Osmium", "Platinum", "Rutile", "Silver", "Steel", "Tin",
			"Titanium", "Zinc"));
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configBlockToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = true;

	@Override
	public String getName() {
		return "mariculture_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material melting recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block melting recipes added."),
				configBlockToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust melting recipes added."),
				configDustToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget casting recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block casting recipes added."),
				configToBlockBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MaricultureHelper helper = MaricultureHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName(".molten", name);
				int baseAmount = MetalRates.INGOT;
				int baseTemp = MaricultureModule.tempFunction.applyAsInt(material);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configMaterialToMoltenBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerCrucibleRecipe(
								miscHelper.getRecipeKey("mariculture.material_to_molten", name),
								materialOredict, 1, moltenName, baseAmount, baseTemp);
					}
					if(!configBlockToMoltenBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerCrucibleRecipe(
									miscHelper.getRecipeKey("mariculture.block_to_molten", name),
									blockOredict, 1, moltenName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), baseTemp);
						}
					}
					if(!configNuggetToMoltenBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerCrucibleRecipe(
									miscHelper.getRecipeKey("mariculture.nugget_to_molten", name),
									nuggetOredict, 1, moltenName, baseAmount/9, baseTemp);
						}
					}
					if(!configDustToMoltenBlacklist.contains(name)) {
						String dustOredict = miscHelper.getOredictName("dust", name);
						if(oredict.contains(dustOredict)) {
							helper.registerCrucibleRecipe(
									miscHelper.getRecipeKey("mariculture.dust_to_molten", name),
									dustOredict, 1, moltenName, baseAmount, baseTemp);
						}
					}
					if(!configToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerIngotCastingRecipe(
								miscHelper.getRecipeKey("mariculture.molten_to_material", name),
								moltenName, baseAmount, materialOredict, 1);
					}
					if(!configToNuggetBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerNuggetCastingRecipe(
									miscHelper.getRecipeKey("mariculture.molten_to_nugget", name),
									moltenName, baseAmount/9, nuggetOredict, 1);
						}
					}
					if(!configToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerBlockCastingRecipe(
									miscHelper.getRecipeKey("mariculture.molten_to_block", name),
									moltenName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), blockOredict, 1);
						}
					}
				}
			}
		}
	}
}
