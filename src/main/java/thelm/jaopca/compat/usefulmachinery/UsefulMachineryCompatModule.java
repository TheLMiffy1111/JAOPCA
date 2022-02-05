package thelm.jaopca.compat.usefulmachinery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.resources.ResourceLocation;
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

@JAOPCAModule(modDependencies = "usefulmachinery")
public class UsefulMachineryCompatModule implements IModule {

	private static final Set<String> GENERAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "copper", "electrum", "gold", "invar", "iron", "lead", "nickel", "silver", "tin", "uranium"));
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();

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
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compactor to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compactor to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compactor to storage block recipes added."),
				configToBlockBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		UsefulMachineryHelper helper = UsefulMachineryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1, 200, 0);
				}
			}
			if(!type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", material.getName());
				if(api.getItemTags().contains(gearLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_gear."+material.getName()),
							materialLocation, 4, gearLocation, 1, 200, 0);
				}
			}
			if(!type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
				if(api.getItemTags().contains(storageBlockLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "usefulmachinery.material_to_storage_block."+material.getName()),
							materialLocation, 9, storageBlockLocation, 1, 200, 0);
				}
			}
		}
	}
}
