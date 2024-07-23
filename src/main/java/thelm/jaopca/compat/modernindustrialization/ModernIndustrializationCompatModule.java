package thelm.jaopca.compat.modernindustrialization;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

@JAOPCAModule(modDependencies = "modern_industrialization")
public class ModernIndustrializationCompatModule implements IModule {

	private static final Set<String> CRYSTAL_BLACKLIST = new TreeSet<>(List.of(
			"coal", "coke", "diamond", "emerald", "lapis", "lignite_coal", "quartz"));
	private static final Set<String> PLATE_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "annealed_copper", "battery_alloy", "beryllium", "blastproof_alloy", "bronze",
			"cadmium", "carbon", "chromium", "copper", "cupronickel", "diamond", "electrum", "emerald", "gold", "invar",
			"iridium", "iron", "kanthal", "lapis", "lead", "nickel", "nuclear_alloy", "platinum", "silicon", "silver",
			"stainless_steel", "steel", "superconductor", "tin", "titanium", "tungsten"));
	private static final Set<String> GEAR_BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "bronze", "copper", "gold", "invar", "iron", "stainless_steel", "steel", "tin", "titanium"));
	private static final Set<String> ROD_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "bronze", "cadmium", "copper", "gold", "he_mox", "he_uranium", "invar", "iron",
			"le_mox", "le_uranium", "stainless_steel", "steel", "tin", "titanium", "tungsten", "uranium"));
	private static final Set<String> BLOCK_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "amethyst", "annealed_copper", "antimony", "battery_alloy", "bauxite", "beryllium",
			"bronze", "chromium", "coal", "coke", "copper", "cupronickel", "diamond", "electrum", "emerald", "gold",
			"he_mox", "he_uranium", "invar", "iridium", "iron", "kanthal", "lapis", "le_mox", "le_uranium", "lead",
			"lignite_coal", "monazite", "neodymium", "nickel", "platinum", "plutonium", "quartz", "redstone", "salt",
			"silicon", "silver", "sodium", "soldering_alloy", "stainless_steel", "steel", "sulfur", "tin", "titanium",
			"tungsten", "uranium", "uranium_235", "uranium_238", "yttrium"));
	private static final Set<String> NUGGET_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "annealed_copper", "antimony", "battery_alloy", "beryllium", "bronze", "chromium",
			"copper", "cupronickel", "electrum", "gold", "he_mox", "he_uranium", "invar", "iridium", "iron", "kanthal",
			"le_mox", "le_uranium", "lead", "nickel", "platinum", "plutonium", "silicon", "silver", "stainless_steel",
			"steel", "superconductor", "tin", "titanium", "tungsten", "uranium", "uranium_235", "uranium_238"));
	private static final Set<String> RAW_BLOCK_BLACKLIST = new TreeSet<>(List.of(
			"antimony", "copper", "gold", "iridium", "iron", "lead", "nickel", "platinum", "silver", "tin", "titanium",
			"tungsten", "uranium"));
	private static final Set<String> DUST_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "annealed_copper", "antimony", "battery_alloy", "bauxite", "beryllium", "brick",
			"bronze", "cadmium", "carbon", "chromium", "coal", "coke", "copper", "cupronickel", "diamond", "electrum",
			"emerald", "fire_clay", "gold", "he_mox", "he_uranium", "invar", "iridium", "iron", "kanthal", "lapis",
			"le_mox", "le_uranium", "lead", "lignite_coal", "manganese", "monazite", "neodymium", "nickel", "platinum",
			"plutonium", "quartz", "redstone", "ruby", "salt", "silicon", "silver", "sodium", "soldering_alloy",
			"stainless_steel", "steel", "sulfur", "superconductor", "tin", "titanium", "tungsten", "uranium",
			"uranium_235", "uranium_238", "yttrium"));
	private static final Set<String> TINY_DUST_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "annealed_copper", "antimony", "battery_alloy", "bauxite", "beryllium", "brick",
			"bronze", "cadmium", "carbon", "chromium", "coal", "copper", "cupronickel", "diamond", "electrum", "emerald",
			"gold", "he_mox", "he_uranium", "invar", "iridium", "iron", "kanthal", "lapis", "le_mox", "le_uranium", "lead",
			"lignite_coal", "manganese", "monazite", "neodymium", "nickel", "platinum", "plutonium", "quartz", "redstone",
			"ruby", "salt", "silicon", "silver", "sodium", "soldering_alloy", "stainless_steel", "steel", "sulfur",
			"superconductor", "tin", "titanium", "tungsten", "uranium", "uranium_235", "uranium_238", "yttrium"));
	private static Set<String> configCompressorToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configImplosionToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockPackingBlacklist = new TreeSet<>();
	private static Set<String> configNuggetPackingBlacklist = new TreeSet<>();
	private static Set<String> configRawStorageBlockPackingBlacklist = new TreeSet<>();
	private static Set<String> configTinyDustPackingBlacklist = new TreeSet<>();
	private static Set<String> configRecyclingBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "modern_industrialization_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.compressorToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to material recipes added."),
				configCompressorToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.implosionToCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have implosion to material recipes added."),
				configImplosionToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have cutting to rod recipes added."),
				configToRodBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.storageBlockPackingMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have storage block packing recipes added."),
				configStorageBlockPackingBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetPackingMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget packing recipes added."),
				configNuggetPackingBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.rawStorageBlockPackingMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have raw storage block packing recipes added."),
				configRawStorageBlockPackingBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tinyDustPackingMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have tiny dust packing recipes added."),
				configTinyDustPackingBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.recyclingMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator recycling recipes added."),
				configRecyclingBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ModernIndustrializationHelper helper = ModernIndustrializationHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Fluid lubricant = BuiltInRegistries.FLUID.get(ResourceLocation.parse("modern_industrialization:lubricant"));
		Item packerBlockTemplate = BuiltInRegistries.ITEM.get(ResourceLocation.parse("modern_industrialization:packer_block_template"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isCrystalline() && !CRYSTAL_BLACKLIST.contains(name) && !configCompressorToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.dust_to_material_compressor", name),
							dustLocation, 1, 1F, materialLocation, 1, 1F, 2, 100);
				}
			}
			if(type.isCrystalline() && !CRYSTAL_BLACKLIST.contains(name) && !configImplosionToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerImplosionCompressorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.dust_to_material_implosion", name),
							new Object[] {
									dustLocation, 1, 1F,
									Blocks.TNT, 1, 1F,
							}, new Object[] {
									materialLocation, 1, 1F,
							}, 1, 10);
				}
			}
			if(!PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.material_to_plate", name),
							materialLocation, 1, 1F, plateLocation, 1, 1F, 2, 200);
				}
			}
			if(!type.isDust() && !ROD_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				if(itemTags.contains(rodLocation)) {
					helper.registerCuttingMachineRecipe(
							miscHelper.getRecipeKey("modern_industrialization.material_to_rod", name),
							materialLocation, 1, 1F, lubricant, 1, 1F, rodLocation, 2, 1F, 2, 200);
				}
			}
			if(!BLOCK_BLACKLIST.contains(name) && !configNuggetPackingBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				if(itemTags.contains(storageBlockLocation)) {
					helper.registerPackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.material_to_storage_block", name),
							new Object[] {
									materialLocation, (material.isSmallStorageBlock() ? 4 : 9), 1F,
									packerBlockTemplate, 1, 0F,
							}, storageBlockLocation, 1, 1F, 2, 200);
					helper.registerUnpackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.storage_block_to_material", name),
							storageBlockLocation, 1, 1F, new Object[] {
									materialLocation, (material.isSmallStorageBlock() ? 4 : 9), 1F,	
							}, 2, 200);
				}
			}
			if(!type.isDust() && !NUGGET_BLACKLIST.contains(name) && !configNuggetPackingBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(nuggetLocation)) {
					helper.registerPackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.nugget_to_material", name),
							new Object[] {
									nuggetLocation, 9, 1F,	
							}, materialLocation, 1, 1F, 2, 200);
					helper.registerUnpackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.material_to_nugget", name),
							materialLocation, 1, 1F, new Object[] {
									nuggetLocation, 9, 1F,	
							}, 2, 200);
				}
			}
			if(type == MaterialType.INGOT && !RAW_BLOCK_BLACKLIST.contains(name) && !configRawStorageBlockPackingBlacklist.contains(name)) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", name);
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", name, "_");
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerPackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.raw_material_to_raw_storage_block", name),
							new Object[] {
									rawMaterialLocation, 9, 1F,	
							}, rawStorageBlockLocation, 1, 1F, 2, 200);
					helper.registerUnpackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.raw_storage_block_to_raw_material", name),
							rawStorageBlockLocation, 1, 1F, new Object[] {
									rawMaterialLocation, 9, 1F,	
							}, 2, 200);
				}
			}
			if(!TINY_DUST_BLACKLIST.contains(name) && !configTinyDustPackingBlacklist.contains(name)) {
				ResourceLocation tinyDustLocation = miscHelper.getTagLocation("tiny_dusts", name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(tinyDustLocation) && itemTags.contains(dustLocation)) {
					helper.registerPackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.tiny_dust_to_dust", name),
							new Object[] {
									tinyDustLocation, 9, 1F,	
							}, dustLocation, 1, 1F, 2, 200);
					helper.registerUnpackerRecipe(
							miscHelper.getRecipeKey("modern_industrialization.dust_to_tiny_dust", name),
							dustLocation, 1, 1F, new Object[] {
									tinyDustLocation, 9, 1F,	
							}, 2, 200);
				}
			}
			if(!type.isDust() && !DUST_BLACKLIST.contains(name) && !configRecyclingBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("tiny_dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.material_to_dust", name),
							materialLocation, 1, 1F, new Object[] {
									dustLocation, 1, 1F,	
							}, 2, 200);
				}
			}
			if(!(NUGGET_BLACKLIST.contains(name) && TINY_DUST_BLACKLIST.contains(name)) && !configRecyclingBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				ResourceLocation tinyDustLocation = miscHelper.getTagLocation("tiny_dusts", name);
				if(itemTags.contains(nuggetLocation) && itemTags.contains(tinyDustLocation)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.nugget_to_tiny_dust", name),
							nuggetLocation, 1, 1F, new Object[] {
									tinyDustLocation, 1, 1F,	
							}, 2, 200);
				}
			}
			if(!(PLATE_BLACKLIST.contains(name) && DUST_BLACKLIST.contains(name)) && !configRecyclingBlacklist.contains(name)) {
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(plateLocation) && itemTags.contains(dustLocation)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.plate_to_dust", name),
							plateLocation, 1, 1F, new Object[] {
									dustLocation, 1, 1F,	
							}, 2, 200);
				}
			}
			if(!(GEAR_BLACKLIST.contains(name) && DUST_BLACKLIST.contains(name)) && !configRecyclingBlacklist.contains(name)) {
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(gearLocation) && itemTags.contains(dustLocation)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.gear_to_dust", name),
							gearLocation, 1, 1F, new Object[] {
									dustLocation, 2, 1F,	
							}, 2, 200);
				}
			}
			if(!(ROD_BLACKLIST.contains(name) && TINY_DUST_BLACKLIST.contains(name)) && !configRecyclingBlacklist.contains(name)) {
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				ResourceLocation tinyDustLocation = miscHelper.getTagLocation("tiny_dusts", name);
				if(itemTags.contains(rodLocation) && itemTags.contains(tinyDustLocation)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("modern_industrialization.rod_to_tiny_dust", name),
							rodLocation, 1, 1F, new Object[] {
									tinyDustLocation, 4, 1F,	
							}, 2, 200);
				}
			}
		}
	}
}
