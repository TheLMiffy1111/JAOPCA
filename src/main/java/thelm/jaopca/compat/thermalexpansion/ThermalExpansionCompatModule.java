package thelm.jaopca.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "thermal_expansion")
public class ThermalExpansionCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"apatite", "bronze", "cinnabar", "constantan", "copper", "diamond", "electrum", "emerald", "ender_pearl",
			"enderium", "gold", "invar", "iron", "lapis", "lead", "lumium", "netherite", "nickel", "niter", "quartz",
			"ruby", "sapphire", "signalum", "silver", "sulfur", "tin"));
	private static final Set<String> DUST_TO_INGOT_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"netherite", "nickel", "signalum", "silver", "tin"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"netherite", "nickel", "signalum", "silver", "tin"));
	private static final Set<String> TO_GEAR_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"netherite", "nickel", "signalum", "silver", "tin"));
	private static final Set<String> TO_COIN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"netherite", "nickel", "signalum", "silver", "tin"));
	private static final Set<String> PACKING_STORAGE_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"amethyst", "apatite", "bronze", "charcoal", "cinnabar", "coal_coke", "coal", "constantan", "copper",
			"diamond", "electrum", "emerald", "enderium", "glowstone", "gold", "gunpowder", "invar", "iron", "lapis",
			"lead", "lumium", "netherite", "nickel", "niter", "quartz", "redstone", "ruby", "sapphire", "signalum",
			"silver", "sulfur", "wood"));
	private static final Set<String> PACKING_NUGGET_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"netherite", "nickel", "signalum", "silver", "tin"));
	private static final Set<String> PACKING_RAW_STORAGE_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "lead", "nickel", "silver", "tin"));
	private static final Set<String> MOLTEN_TO_INGOT_BLACKLIST = new TreeSet<>();
	private static final Set<String> TO_ROD_BLACKLIST = new TreeSet<>();
	private static final Set<String> CREATE_BLACKLIST = new TreeSet<>();
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configDustToIngotBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToCoinBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToCoinBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToRawStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToRawMaterialBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToIngotBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configCreateToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configCreateToIngotBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("alltheores")) {
			Collections.addAll(TO_DUST_BLACKLIST, "osmium", "platinum", "zinc");
		}
		if(ModList.get().isLoaded("thermal_integration")) {
			if(ModList.get().isLoaded("create")) {
				Collections.addAll(PACKING_RAW_STORAGE_BLOCK_BLACKLIST, "zinc");
				//Collections.addAll(CREATE_BLACKLIST, "copper", "gold", "iron", "lead", "zinc");
				//if(ModList.get().isLoaded("immersiveengineering")) {
				//	Collections.addAll(CREATE_BLACKLIST, "aluminium", "aluminum", "uranium");
				//}
			}
			if(ModList.get().isLoaded("immersiveengineering")) {
				Collections.addAll(TO_DUST_BLACKLIST, "aluminium", "aluminum", "uranium");
				Collections.addAll(DUST_TO_INGOT_BLACKLIST, "aluminium", "aluminum", "uranium");
				Collections.addAll(TO_PLATE_BLACKLIST, "aluminium", "aluminum", "steel", "uranium");
				Collections.addAll(PACKING_STORAGE_BLOCK_BLACKLIST, "aluminium", "aluminum", "uranium");
				Collections.addAll(PACKING_NUGGET_BLACKLIST, "aluminium", "aluminum", "uranium");
				Collections.addAll(PACKING_RAW_STORAGE_BLOCK_BLACKLIST, "aluminium", "aluminum", "uranium");
			}
			if(ModList.get().isLoaded("tconstruct")) {
				Collections.addAll(MOLTEN_TO_INGOT_BLACKLIST, "amethyst_bronze", "bronze", "cobalt", "constantan", "copper",
						"debris", "electrum", "gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn",
						"netherite", "netherite_scrap", "nickel", "pig_iron", "queens_slime", "rose_gold", "silver",
						"slimesteel", "tin");
				Collections.addAll(PACKING_STORAGE_BLOCK_BLACKLIST, "amethyst_bronze", "cobalt", "hepatizon", "manyullyn",
						"pig_iron", "queens_slime", "rose_gold", "slimesteel");
				Collections.addAll(PACKING_NUGGET_BLACKLIST, "amethyst_bronze", "cobalt", "hepatizon", "manyullyn",
						"pig_iron", "queens_slime", "rose_gold", "slimesteel");
				Collections.addAll(PACKING_RAW_STORAGE_BLOCK_BLACKLIST);
				if(ModList.get().isLoaded("immersiveengineering")) {
					Collections.addAll(MOLTEN_TO_INGOT_BLACKLIST, "aluminium", "aluminum");
					Collections.addAll(TO_ROD_BLACKLIST, "aluminium", "aluminum", "iron", "steel");
				}
			}
		}
	}

	@Override
	public String getName() {
		return "thermal_expansion_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pulverizer to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have smelter to ingot recipes added."),
				configDustToIngotBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToCoinMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press material to coin recipes added."),
				configMaterialToCoinBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToCoinMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press nugget to coin recipes added."),
				configNuggetToCoinBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press material to storage block recipes added."),
				configToStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press nugget to material recipes added."),
				configNuggetToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRawStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press raw material to raw storage block recipes added."),
				configToRawStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.storageBlockToMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press storage block to material recipes added."),
				configStorageBlockToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press material to nugget recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRawMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press raw storage block to raw material recipes added."),
				configToRawMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have chiller to ingot recipes added."),
				configMoltenToIngotBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have chiller to rod recipes added."),
				configToRodBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.createToNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have create centrifuge compat recipes added."),
				configCreateToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.createToIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have create smelter compat recipes added."),
				configCreateToIngotBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThermalExpansionHelper helper = ThermalExpansionHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Set<ResourceLocation> fluidTags = api.getFluidTags();
		Item richSlag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:rich_slag"));
		Item gearDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_gear_die"));
		Item coinDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_coin_die"));
		Item packingDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_packing_3x3_die"));
		Item unpackingDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_unpacking_die"));
		Item ingotCast = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:chiller_ingot_cast"));
		Item rodCast = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:chiller_rod_cast"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerPulverizerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_dust."+name),
							materialLocation, 1, new Object[] {
									dustLocation,
							}, 2000, 0F);
				}
			}
			if(type.isIngot() && !DUST_TO_INGOT_BLACKLIST.contains(name) && !configDustToIngotBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerSmelterRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.dust_to_material."+name),
							new Object[] {
									dustLocation,
							}, new Object[] {
									materialLocation,
							}, 1600, 0F);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_plate."+name),
							materialLocation, 1, plateLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !TO_GEAR_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(gearLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_gear."+name),
							materialLocation, 4, gearDie, 1, gearLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !TO_COIN_BLACKLIST.contains(name) && !configMaterialToCoinBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", name);
				if(itemTags.contains(coinLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_coin."+name),
							materialLocation, 1, coinDie, 1, coinLocation, 3,
							2400, 0F);
				}
			}
			if(type.isIngot() && !TO_COIN_BLACKLIST.contains(name) && !configNuggetToCoinBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", name);
				if(itemTags.contains(nuggetLocation) && itemTags.contains(coinLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.nugget_to_coin."+name),
							nuggetLocation, 3, coinDie, 1, coinLocation, 1,
							800, 0F);
				}
			}
			if(!PACKING_STORAGE_BLOCK_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				if(itemTags.contains(storageBlockLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_storage_block."+name),
							materialLocation, 9, packingDie, 1, storageBlockLocation, 1,
							400, 0F);
				}
			}
			if(!PACKING_NUGGET_BLACKLIST.contains(name) && !configNuggetToMaterialBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(nuggetLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.nugget_to_material."+name),
							nuggetLocation, 9, packingDie, 1, materialLocation, 1,
							400, 0F);
				}
			}
			if(!PACKING_RAW_STORAGE_BLOCK_BLACKLIST.contains(name) && !configToRawStorageBlockBlacklist.contains(name)) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", name);
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", name, "_");
				if(itemTags.contains(rawMaterialLocation) && itemTags.contains(rawStorageBlockLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.raw_material_to_raw_storage_block."+name),
							rawMaterialLocation, 9, packingDie, 1, rawStorageBlockLocation, 1,
							400, 0F);
				}
			}
			if(!PACKING_STORAGE_BLOCK_BLACKLIST.contains(name) && !configStorageBlockToMaterialBlacklist.contains(name)) {
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(storageBlockLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.storage_block_to_material."+name),
							storageBlockLocation, 1, unpackingDie, 1, materialLocation, 9,
							400, 0F);
				}
			}
			if(!PACKING_STORAGE_BLOCK_BLACKLIST.contains(name) && !configStorageBlockToMaterialBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				if(itemTags.contains(nuggetLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_nugget."+name),
							materialLocation, 1, unpackingDie, 1, nuggetLocation, 9,
							400, 0F);
				}
			}
			if(!PACKING_RAW_STORAGE_BLOCK_BLACKLIST.contains(name) && !configToRawMaterialBlacklist.contains(name)) {
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", name, "_");
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", name);
				if(itemTags.contains(rawMaterialLocation) && itemTags.contains(rawStorageBlockLocation)) {
					helper.registerPressRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.raw_storage_block_to_raw_material."+name),
							rawStorageBlockLocation, 1, unpackingDie, 1, rawMaterialLocation, 9,
							400, 0F);
				}
			}
			if(type.isIngot() && !MOLTEN_TO_INGOT_BLACKLIST.contains(name) && !configMoltenToIngotBlacklist.contains(name)) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(fluidTags.contains(moltenLocation)) {
					helper.registerChillerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.molten_to_material."+name),
							moltenLocation, 90, ingotCast, 1, materialLocation, 1,
							4800, 0F);
				}
			}
			if(!TO_ROD_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				if(fluidTags.contains(moltenLocation) && itemTags.contains(rodLocation)) {
					helper.registerChillerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.molten_to_rod."+name),
							moltenLocation, (type.isIngot() ? 45 : 50), rodCast, 1, rodLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !CREATE_BLACKLIST.contains(name) && !configCreateToNuggetBlacklist.contains(name)) {
				ResourceLocation crushedOreLocation = miscHelper.getTagLocation("create:crushed_ores", name);
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				if(itemTags.contains(crushedOreLocation) && itemTags.contains(nuggetLocation)) {
					helper.registerCentrifugeRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.create_crushed_ore_to_nugget."+name),
							crushedOreLocation, 1, new Object[] {
									nuggetLocation, 9,
							}, 1600, 0F);
				}
			}
			if(type.isIngot() && !CREATE_BLACKLIST.contains(name) && !configCreateToIngotBlacklist.contains(name)) {
				ResourceLocation crushedOreLocation = miscHelper.getTagLocation("create:crushed_ores", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(crushedOreLocation)) {
					helper.registerSmelterRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.create_crushed_ore_to_material."+name),
							new Object[] {
									crushedOreLocation, 1,
							}, new Object[] {
									materialLocation, 1,
							}, 1600, 0.1F);
				}
			}
		}
	}
}
