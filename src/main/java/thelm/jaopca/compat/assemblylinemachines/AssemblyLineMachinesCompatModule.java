package thelm.jaopca.compat.assemblylinemachines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import me.haydenb.assemblylinemachines.registry.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

@JAOPCAModule(modDependencies = "assemblylinemachines")
public class AssemblyLineMachinesCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminium", "aluminum", "amethyst", "ardite", "brass", "bronze", "chromium", "coal", "cobalt",
			"constantan", "copper", "diamond", "electrum", "emerald", "flerovium", "gold", "invar", "iron",
			"lapis", "lead", "manyullyn", "netherite", "netherite_scrap", "nickel", "osmium", "platinum",
			"rose_gold", "silver", "steel", "tin", "titanium", "tungsten", "uranium", "zinc"));
	private static final Set<String> TO_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminium", "aluminum", "amethyst", "ardite", "attuned_titanium", "brass", "bronze", "chromium",
			"coal", "cobalt", "constantan", "copper", "diamond", "electrum", "emerald", "energized_gold",
			"flerovium", "gold", "invar", "iron", "lapis", "lead", "manyullyn", "mystium", "nickel", "novasteel",
			"osmium", "platinum", "redstone", "rose_gold", "silver", "steel", "tin", "titanium", "tungsten",
			"uranium", "zinc"));
	private static final Set<String> TO_RAW_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"chromium", "flerovium", "titanium"));
	private static final Set<String> TO_MATERIAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminium", "aluminum", "ardite", "brass", "bronze", "chromium", "cobalt", "constantan", "copper",
			"electrum", "flerovium", "gold", "invar", "iron", "lead", "manyullyn", "nickel", "osmium", "platinum",
			"rose_gold", "silver", "steel", "tin", "titanium", "tungsten", "uranium", "zinc"));
	private static final Set<String> METAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminium", "aluminum", "ardite", "attuned_titanium", "brass", "bronze", "chromium", "cobalt",
			"constantan", "copper", "electrum", "energized_gold", "flerovium", "gold", "graphene", "invar",
			"iron", "lead", "manyullyn", "mystium", "nickel", "novasteel", "osmium", "platinum", "pure_copper",
			"pure_gold", "pure_iron", "pure_steel", "pure_titanium", "rose_gold", "silver", "steel", "tin",
			"titanium", "tungsten", "uranium", "zinc"));
	private static final Set<String> HAMMER_BLACKLIST = new TreeSet<>(Arrays.asList(
			"attuned_titanium", "chromium", "copper", "energized_gold", "flerovium", "gold", "iron", "mystium",
			"novasteel", "pure_copper", "pure_gold", "pure_iron", "pure_steel", "pure_titanium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToRawStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configHammerToPlateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "assemblylinemachines_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have grinder to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pneumatic compressor to storage block recipes added."),
				configToStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRawStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pneumatic compressor to raw storage block recipes added."),
				configToRawStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pneumatic compressor nugget to material recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pneumatic compressor to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pneumatic compressor to rod recipes added."),
				configToRodBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pneumatic compressor to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.hammerToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hammer to plate recipes added."),
				configHammerToPlateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AssemblyLineMachinesHelper helper = AssemblyLineMachinesHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		ResourceLocation hammerLocation = new ResourceLocation("assemblylinemachines:hammers");
		Item plateMold = Registry.getItem("plate_mold");
		Item rodMold = Registry.getItem("rod_mold");
		Item gearMold = Registry.getItem("gear_mold");
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerGrinderRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_dust."+name),
							materialLocation, dustLocation, 1, 4, 2, false, 0);
				}
			}
			if(!TO_BLOCK_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				if(itemTags.contains(storageBlockLocation)) {
					helper.registerPneumaticRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_storage_block."+name),
							materialLocation, 9, Items.AIR, storageBlockLocation, 1, 9);
				}
			}
			if(type == MaterialType.INGOT && !TO_RAW_BLOCK_BLACKLIST.contains(name) && !configToRawStorageBlockBlacklist.contains(name)) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", name);
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", name, "_");
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerPneumaticRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.raw_material_to_raw_storage_block."+name),
							rawMaterialLocation, 9, Items.AIR, rawStorageBlockLocation, 1, 9);
				}
			}
			if(!type.isDust() && !TO_MATERIAL_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(nuggetLocation)) {
					helper.registerPneumaticRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.nugget_to_material."+name),
							nuggetLocation, 9, Items.AIR, materialLocation, 1, 5);
				}
			}
			if(type.isIngot() && !METAL_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerPneumaticRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_plate."+name),
							materialLocation, 1, plateMold, plateLocation, 1, 7);
				}
			}
			if(type.isIngot() && !METAL_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				if(itemTags.contains(rodLocation)) {
					helper.registerPneumaticRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_rod."+name),
							materialLocation, 1, rodMold, rodLocation, 1, 8);
				}
			}
			if(type.isIngot() && !METAL_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(plateLocation) && itemTags.contains(gearLocation)) {
					helper.registerPneumaticRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.plate_to_gear."+name),
							plateLocation, 4, rodMold, gearLocation, 1, 9);
				}
			}
			if(type.isIngot() && !HAMMER_BLACKLIST.contains(name) && !configHammerToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					api.registerShapelessRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_plate_hammer."+name),
							plateLocation, 1, new Object[] {
									materialLocation, hammerLocation,
							});
				}
			}
		}
	}
}
