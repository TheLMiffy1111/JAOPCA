package thelm.jaopca.compat.indreb;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

@JAOPCAModule(modDependencies = "indreb@[1.19.2-0.14.2,)")
public class IndRebCompatModule implements IModule {

	private static final Set<String> TO_BLOCK_BLACKLIST = new TreeSet<>(List.of(
			"bone", "brick", "bronze", "coal", "copper", "diamond", "emerald", "glowstone", "gold", "iron", "lapis",
			"nether_brick", "netherite", "quartz", "redstone", "silver", "steel", "tin"));
	private static final Set<String> TO_RAW_BLOCK_BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "uranium"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(List.of(
			"bronze", "copper", "gold", "iron", "lead", "steel", "tin"));
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToRawStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configHammerToPlateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "indreb_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to storage block recipes added."),
				configToStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRawStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to raw storage block recipes added."),
				configToRawStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have roller to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.hammerToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hammer to plate recipes added."),
				configHammerToPlateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IndRebHelper helper = IndRebHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Item hammer = ForgeRegistries.ITEMS.getValue(new ResourceLocation("indreb:hammer"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!TO_BLOCK_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				if(itemTags.contains(storageBlockLocation)) {
					helper.registerCompressingRecipe(
							new ResourceLocation("jaopca", "indreb.material_to_storage_block."+name),
							materialLocation, 9,
							storageBlockLocation, 1,
							180, 8, 0.3F);
				}
			}
			if(type == MaterialType.INGOT && !TO_RAW_BLOCK_BLACKLIST.contains(name) && !configToRawStorageBlockBlacklist.contains(name)) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", name);
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", "_", name);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerCompressingRecipe(
							new ResourceLocation("jaopca", "indreb.raw_material_to_raw_storage_block."+name),
							rawMaterialLocation, 9,
							rawStorageBlockLocation, 1,
							180, 8, 0.3F);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerRollingRecipe(
							new ResourceLocation("jaopca", "indreb.material_to_plate."+name),
							materialLocation, 1,
							plateLocation, 1,
							180, 8, 0.5F);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configHammerToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					api.registerShapelessRecipe(
							new ResourceLocation("jaopca", "indreb.material_to_plate_hammer."+name),
							"indreb", plateLocation, 1, new Object[] {
									hammer, materialLocation,
							});
				}
			}
		}
	}
}
