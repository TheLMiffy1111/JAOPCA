package thelm.jaopca.compat.usefulmachinery;

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

@JAOPCAModule(modDependencies = "usefulmachinery")
public class UsefulMachineryCompatModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "bronze", "copper", "diamond", "electrum", "enderium", "gold", "invar",
			"iron", "lead", "nickel", "platinum", "signalum", "silver", "steel", "tin", "uranium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configHammerToPlateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "usefulmachinery_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have crushing to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compacting to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compacting to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compacting to storage block recipes added."),
				configToStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.hammerToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hammering recipes added."),
				configHammerToPlateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		UsefulMachineryHelper helper = UsefulMachineryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Item hammer = ForgeRegistries.ITEMS.getValue(new ResourceLocation("usefulfoundation:hammer"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_dust."+name),
							materialLocation, dustLocation, 1, 200);
				}
			}
			if(type.isIngot() && !BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_plate."+name),
							materialLocation, 1, plateLocation, 1, 200, "plate");
				}
			}
			if(type.isIngot() && !BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(gearLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_gear."+name),
							materialLocation, 4, gearLocation, 1, 200, "gear");
				}
			}
			if(!BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				if(itemTags.contains(storageBlockLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_storage_block."+name),
							materialLocation, 9, storageBlockLocation, 1, 250, "block");
				}
			}
			if(type.isIngot() && !BLACKLIST.contains(name) && !configHammerToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					api.registerShapelessRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_plate_hammer."+name),
							plateLocation, 1, new Object[] {
									materialLocation, hammer,
							});
				}
			}
		}
	}
}
