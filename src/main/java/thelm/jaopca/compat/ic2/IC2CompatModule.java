package thelm.jaopca.compat.ic2;

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

@JAOPCAModule(modDependencies = "ic2")
public class IC2CompatModule implements IModule {

	private static final Set<String> MATERIAL_TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Bronze", "Clay", "Coal", "Copper", "Diamond", "Emerald", "EnderEye", "EnderPearl", "Gold", "Iron",
			"Lapis", "Lead", "Obsidian", "Silver", "Steel", "Tin"));
	private static final Set<String> PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Bronze", "Copper", "Gold", "Iron", "Lapis", "Lead", "Obsidian", "Steel", "Tin"));
	private static final Set<String> TO_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Bronze", "Coal", "Copper", "Gold", "Iron", "Lapis", "Lead", "Redstone", "Silver", "Steel", "Tin"));
	private static final Set<String> TINY_DUST_TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Bronze", "Copper", "Gold", "Iron", "Lapis", "Lead", "Lithium", "Obsidian", "Silver", "Sulfur", "Tin"));
	private static Set<String> configMaterialToDustBlacklist = new TreeSet<>();
	private static Set<String> configIngotToPlateBlacklist = new TreeSet<>();
	private static Set<String> configDustToPlateBlacklist = new TreeSet<>();
	private static Set<String> configBlockToPlateBlacklist = new TreeSet<>();
	private static Set<String> configPlateToDustBlacklist = new TreeSet<>();
	private static Set<String> configToDensePlateBlacklist = new TreeSet<>();
	private static Set<String> configDensePlateToDustBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();
	private static Set<String> configTinyDustToDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "ic2_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator material to dust recipes added."),
				configMaterialToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.ingotToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have metal former rolling to plate recipes added."),
				configIngotToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to plate recipes added."),
				configDustToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have metal cutting machine to plate recipes added."),
				configBlockToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator plate to dust recipes added."),
				configPlateToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDensePlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to dense plate recipes added."),
				configToDensePlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.densePlateToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator dense plate to dust recipes added."),
				configDensePlateToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to block recipes added."),
				configToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tinyDustToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to dust recipes added."),
				configTinyDustToDustBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IC2Helper helper = IC2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !MATERIAL_TO_DUST_BLACKLIST.contains(name) && !configMaterialToDustBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("ic2.material_to_dust", name),
							materialOredict, 1, dustOredict, 1);
				}
			}
			if(type.isIngot() && !PLATE_BLACKLIST.contains(name) && !configIngotToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict)) {
					helper.registerMetalFormerRollingRecipe(
							miscHelper.getRecipeKey("ic2.material_to_plate", name),
							materialOredict, 1, plateOredict, 1);
				}
			}
			if(!type.isIngot() && !PLATE_BLACKLIST.contains(name) && !configDustToPlateBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(dustOredict) && oredict.contains(plateOredict)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("ic2.dust_to_plate", name),
							dustOredict, 1, plateOredict, 1);
				}
			}
			if(!PLATE_BLACKLIST.contains(name) && !configBlockToPlateBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(blockOredict) && oredict.contains(plateOredict)) {
					helper.registerBlockCutterRecipe(
							miscHelper.getRecipeKey("ic2.block_to_plate", name),
							blockOredict, 1, plateOredict, material.isSmallStorageBlock() ? 4 : 9, 5);
				}
			}
			if(!PLATE_BLACKLIST.contains(name) && !configPlateToDustBlacklist.contains(name)) {
				String plateOredict = miscHelper.getOredictName("plate", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(plateOredict) && oredict.contains(dustOredict)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("ic2.plate_to_dust", name),
							plateOredict, 1, dustOredict, 1);
				}
			}
			if(!PLATE_BLACKLIST.contains(name) && !configToDensePlateBlacklist.contains(name)) {
				String plateOredict = miscHelper.getOredictName("plate", name);
				String densePlateOredict = miscHelper.getOredictName("plateDense", name);
				if(oredict.contains(plateOredict) && oredict.contains(densePlateOredict)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("ic2.plate_to_dense_plate", name),
							plateOredict, 9, densePlateOredict, 1);
				}
			}
			if(!PLATE_BLACKLIST.contains(name) && !configDensePlateToDustBlacklist.contains(name)) {
				String densePlateOredict = miscHelper.getOredictName("plateDense", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(densePlateOredict) && oredict.contains(dustOredict)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("ic2.dense_plate_to_dust", name),
							densePlateOredict, 1, dustOredict, 9);
				}
			}
			if(!TO_BLOCK_BLACKLIST.contains(name) && !configToBlockBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String blockOredict = miscHelper.getOredictName("block", name);
				if(oredict.contains(blockOredict)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("ic2.material_to_block", name),
							materialOredict, material.isSmallStorageBlock() ? 4 : 9, blockOredict, 1);
				}
			}
			if(!TINY_DUST_TO_DUST_BLACKLIST.contains(name) && !configTinyDustToDustBlacklist.contains(name)) {
				String tinyDustOredict = miscHelper.getOredictName("dustTiny", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(tinyDustOredict) && oredict.contains(dustOredict)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("ic2.tiny_dust_to_dust", name),
							tinyDustOredict, 9, dustOredict, 1);
				}
			}
		}
	}
}
