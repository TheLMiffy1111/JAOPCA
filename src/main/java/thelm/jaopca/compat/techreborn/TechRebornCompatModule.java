package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
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

@JAOPCAModule(modDependencies = "techreborn")
public class TechRebornCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminium", "aluminum", "brass", "bronze", "certus_quartz", "charcoal", "chromium", "clay", "coal", "diamond",
			"electrum", "emerald", "ender_eye", "ender_pearl", "flint", "fluix", "invar", "nickel", "peridot", "platinum",
			"prismarine", "quartz", "red_garnet", "ruby", "sapphire", "steel", "titanium", "yellow_garnet", "zinc"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"advanced_alloy", "aluminium", "aluminum", "brass", "bronze", "chromium", "coal", "copper", "diamond", "electrum",
			"emerald", "gold", "invar", "iridium", "iridium_alloy", "iron", "lapis", "lazurite", "lead", "nickel", "obsidian",
			"peridot", "platinum", "quartz", "red_garnet", "redstone", "refined_iron", "ruby", "sapphire", "silver", "steel",
			"tin", "titanium", "tungsten", "tungstensteel", "wood", "yellow_garnet", "zinc"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"amethyst", "diamond", "emerald", "peridot", "red_garnet", "ruby", "sapphire", "yellow_garnet"));
	private static Set<String> configMaterialToDustBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToDustBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToPlateBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToPlateBlacklist = new TreeSet<>();
	private static Set<String> configDustToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "techreborn_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have grinder material to dust recipes added."),
				configMaterialToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.storageBlockToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have grinder block to dust recipes added."),
				configStorageBlockToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor material to plate recipes added."),
				configMaterialToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.storageBlockToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor block to plate recipes added."),
				configStorageBlockToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor dust to plate recipes added."),
				configDustToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have implosion compressor to material recipes added."),
				configToCrystalBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Item darkAshes = BuiltInRegistries.ITEM.get(ResourceLocation.parse("techreborn:dark_ashes_dust"));
		Item enderEyeSmallDust = BuiltInRegistries.ITEM.get(ResourceLocation.parse("techreborn:ender_eye_small_dust"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configMaterialToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.material_to_dust", name),
							materialLocation, 1, dustLocation, 1, 4, 200);
				}
			}
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configStorageBlockToDustBlacklist.contains(name)) {
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(storageBlockLocation) && itemTags.contains(dustLocation)) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.storage_block_to_dust", name),
							storageBlockLocation, 1, dustLocation, material.isSmallStorageBlock() ? 4 : 9, 4, 1500);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configMaterialToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.material_to_plate", name),
							materialLocation, 1, plateLocation, 1, 10, 300);
				}
			}
			if(!type.isDust() && !TO_PLATE_BLACKLIST.contains(name) && !configStorageBlockToPlateBlacklist.contains(name)) {
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(storageBlockLocation) && itemTags.contains(plateLocation)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.storage_block_to_plate", name),
							storageBlockLocation, 1, plateLocation, material.isSmallStorageBlock() ? 4 : 9, 10, 300);
				}
			}
			if(!type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configDustToPlateBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(dustLocation) && itemTags.contains(plateLocation)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.dust_to_plate", name),
							dustLocation, 1, plateLocation, 1, 10, 300);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerImplosionCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.dust_to_material_tnt", name),
							dustLocation, 4, Blocks.TNT, 16,
							materialLocation, 3, darkAshes, 12,
							30, 2000);
					helper.registerImplosionCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.dust_to_material_end_crystal", name),
							dustLocation, 4, Items.END_CRYSTAL, 4,
							materialLocation, 3, enderEyeSmallDust, 4,
							30, 2000);
				}
			}
		}
	}
}
