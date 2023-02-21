package thelm.jaopca.compat.abyssalcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

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

@JAOPCAModule(modDependencies = "abyssalcraft")
public class AbyssalCraftCompatModule implements IModule {

	private static final Set<String> BLOCK_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "NaturalAluminum", "Tin"));
	private static final Set<String> MATERIAL_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "Magnesium", "NaturalAluminum", "Tin", "Zinc"));
	private static final Set<String> NUGGET_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "Magnesium", "NaturalAluminum", "Tin", "Zinc"));
	private static final Set<String> DUST_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Copper", "Gold", "Iron", "NaturalAluminum", "Tin"));
	private static final Set<String> TRANSMUTE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "Magnesium", "NaturalAluminum", "Tin", "Zinc"));
	private static Set<String> configBlockToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configDustToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "abyssalcraft_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystallizer block to crystal recipes added."),
				configBlockToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystallizer material to crystal recipes added."),
				configMaterialToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystallizer nugget to crystal recipes added."),
				configNuggetToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystallizer dust to crystal recipes added."),
				configDustToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.transmuteToNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have transmutator to nugget recipes added."),
				configToNuggetBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AbyssalCraftHelper helper = AbyssalCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !BLOCK_TO_CRYSTAL_BLACKLIST.contains(name) && !configBlockToCrystalBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String crystalOredict = miscHelper.getOredictName("crystal", name);
				if(oredict.contains(blockOredict) && oredict.contains(crystalOredict)) {
					helper.registerCrystallizationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.block_to_crystal", name),
							blockOredict, crystalOredict, 4, 0.9F);
				}
			}
			if(type.isIngot() && !MATERIAL_TO_CRYSTAL_BLACKLIST.contains(name) && !configMaterialToCrystalBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), name);
				String crystalShardOredict = miscHelper.getOredictName("crystalShard", name);
				if(oredict.contains(crystalShardOredict)) {
					helper.registerCrystallizationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.material_to_crystal_shard", name),
							materialOredict, crystalShardOredict, 4, 0.1F);
				}
			}
			if(type.isIngot() && !NUGGET_TO_CRYSTAL_BLACKLIST.contains(name) && !configNuggetToCrystalBlacklist.contains(name)) {
				String nuggetOredict = miscHelper.getOredictName("nugget", name);
				String crystalShardOredict = miscHelper.getOredictName("crystalShard", name);
				if(oredict.contains(nuggetOredict) && oredict.contains(crystalShardOredict)) {
					helper.registerCrystallizationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.nugget_to_crystal_shard", name),
							nuggetOredict, crystalShardOredict, 1, 0.1F);
				}
			}
			if(type.isIngot() && !DUST_TO_CRYSTAL_BLACKLIST.contains(name) && !configDustToCrystalBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String crystalShardOredict = miscHelper.getOredictName("crystalShard", name);
				if(oredict.contains(dustOredict) && oredict.contains(crystalShardOredict)) {
					helper.registerCrystallizationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.dust_to_crystal_shard", name),
							dustOredict, crystalShardOredict, 4, 0.1F);
				}
			}
			if(type.isIngot() && !TRANSMUTE_BLACKLIST.contains(name) && !configToNuggetBlacklist.contains(name)) {
				String nuggetOredict = miscHelper.getOredictName("nugget", name);
				String crystalShardOredict = miscHelper.getOredictName("crystalShard", name);
				if(oredict.contains(nuggetOredict) && oredict.contains(crystalShardOredict)) {
					helper.registerTransmutationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.crystal_shard_to_nugget", name),
							crystalShardOredict, nuggetOredict, 1, 0.2F);
				}
			}
		}
	}
}
