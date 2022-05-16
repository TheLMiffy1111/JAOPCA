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
	private static final Set<String> PRESS_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"));
	private static final Set<String> PACKING_STORAGE_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"amethyst", "apatite", "bronze", "charcoal", "cinnabar", "coal_coke", "coal", "constantan", "copper",
			"diamond", "electrum", "emerald", "enderium", "gold", "gunpowder", "invar", "iron", "lapis", "lead",
			"lumium", "netherite", "nickel", "niter", "quartz", "redstone", "ruby", "sapphire", "signalum", "silver",
			"sulfur", "wood"));
	private static final Set<String> PACKING_RAW_STORAGE_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "lead", "nickel", "silver", "tin"));
	private static final Set<String> MOLTEN_TO_INGOT_BLACKLIST = new TreeSet<>();
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configDustToIngotBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToCoinBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToCoinBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToRawStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToRawMaterialBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToIngotBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("alltheores")) {
			Collections.addAll(TO_DUST_BLACKLIST, "osmium", "platinum", "zinc");
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
				config.getDefinedStringList("recipes.toStorageBlockBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press material to storage block recipes added."),
				configToStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRawStorageBlockBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press raw material to raw storage block recipes added."),
				configToRawStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press storage block to material recipes added."),
				configStorageBlockToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRawStorageBlockBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press raw storage block to raw material recipes added."),
				configToRawMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.chillerToIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have chiller to ingot recipes added."),
				configMoltenToIngotBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThermalExpansionHelper helper = ThermalExpansionHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Item richSlag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:rich_slag"));
		Item gearDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_gear_die"));
		Item coinDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_coin_die"));
		Item packingDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_packing_3x3_die"));
		Item unpackingDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_unpacking_die"));
		Item ingotCast = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:chiller_ingot_cast"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerPulverizerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_dust."+material.getName()),
							materialLocation, 1, new Object[] {
									dustLocation,
							}, 2000, 0F);
				}
			}
			if(type.isIngot() && !DUST_TO_INGOT_BLACKLIST.contains(name) && !configDustToIngotBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerSmelterRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.dust_to_material."+material.getName()), new Object[] {
									dustLocation,
							}, new Object[] {
									materialLocation,
							}, 1600, 0F);
				}
			}
			if(type.isIngot() && !PRESS_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !PRESS_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", material.getName());
				if(api.getItemTags().contains(gearLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_gear."+material.getName()),
							materialLocation, 4, gearDie, 1, gearLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !PRESS_BLACKLIST.contains(name) && !configMaterialToCoinBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
				if(api.getItemTags().contains(coinLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_coin."+material.getName()),
							materialLocation, 1, coinDie, 1, coinLocation, 3,
							2400, 0F);
				}
			}
			if(type.isIngot() && !PRESS_BLACKLIST.contains(name) && !configNuggetToCoinBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
				if(api.getItemTags().contains(nuggetLocation) && api.getItemTags().contains(coinLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.nugget_to_coin."+material.getName()),
							nuggetLocation, 3, coinDie, 1, coinLocation, 1,
							800, 0F);
				}
			}
			if(!PACKING_STORAGE_BLOCK_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
				if(api.getItemTags().contains(storageBlockLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_storage_block."+material.getName()),
							materialLocation, 9, packingDie, 1, storageBlockLocation, 1,
							400, 0F);
				}
			}
			if(!PACKING_RAW_STORAGE_BLOCK_BLACKLIST.contains(name) && !configToRawStorageBlockBlacklist.contains(name)) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				if(api.getItemTags().contains(rawMaterialLocation) && api.getItemTags().contains(rawStorageBlockLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.raw_material_to_raw_storage_block."+material.getName()),
							rawMaterialLocation, 9, packingDie, 1, rawStorageBlockLocation, 1,
							400, 0F);
				}
			}
			if(!PACKING_STORAGE_BLOCK_BLACKLIST.contains(name) && !configStorageBlockToMaterialBlacklist.contains(name)) {
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getItemTags().contains(storageBlockLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.storage_block_to_material."+material.getName()),
							storageBlockLocation, 1, unpackingDie, 1, materialLocation, 9,
							400, 0F);
				}
			}
			if(!PACKING_RAW_STORAGE_BLOCK_BLACKLIST.contains(name) && !configToRawMaterialBlacklist.contains(name)) {
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				if(api.getItemTags().contains(rawMaterialLocation) && api.getItemTags().contains(rawStorageBlockLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.raw_storage_block_to_raw_material."+material.getName()),
							rawStorageBlockLocation, 1, unpackingDie, 1, rawMaterialLocation, 9,
							400, 0F);
				}
			}
			if(type.isIngot() && !MOLTEN_TO_INGOT_BLACKLIST.contains(name) && !configMoltenToIngotBlacklist.contains(name)) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getFluidTags().contains(moltenLocation)) {
					helper.registerChillerRecipe(new ResourceLocation("jaopca", "thermal_expansion.molten_to_material."+material.getName()),
							moltenLocation, 90, ingotCast, 1, materialLocation, 1,
							5000, 0F);
				}
			}
		}
	}
}
