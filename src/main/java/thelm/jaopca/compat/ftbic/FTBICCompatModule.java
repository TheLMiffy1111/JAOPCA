package thelm.jaopca.compat.ftbic;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

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

@JAOPCAModule(modDependencies = "ftbic")
public class FTBICCompatModule implements IModule {

	private static boolean addToFTBICMaterials = false;
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "ftbic_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		config.getDefinedBoolean("recipes.addToFTBICMaterials", addToFTBICMaterials, "Should materials be directly added to FTBIC's material list to generate recipes instead of having JAOPCA do so.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have roller to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have roller to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have extruder to rod recipes added."),
				configToRodBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FTBICHelper helper = FTBICHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Set<String> generalBlacklist = helper.getBlacklist();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			boolean addToMaterials = false;
			if(!type.isDust() && !generalBlacklist.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					if(!addToFTBICMaterials) {
						helper.registerMaceratingRecipe(
								new ResourceLocation("jaopca", "ftbic.material_to_dust."+name),
								materialLocation, 1, new Object[] {
										dustLocation, 1, 1D,
								}, 1D);
					}
					else {
						addToMaterials = true;
					}
				}
			}
			if(!type.isDust() && !generalBlacklist.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					if(!addToFTBICMaterials) {
						helper.registerRollingRecipe(
								new ResourceLocation("jaopca", "ftbic.material_to_plate."+name),
								materialLocation, 1, plateLocation, 2, 1D);
					}
					else {
						addToMaterials = true;
					}
				}
			}
			if(type.isIngot() && !generalBlacklist.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(plateLocation) && itemTags.contains(gearLocation)) {
					if(!addToFTBICMaterials) {
						helper.registerRollingRecipe(
								new ResourceLocation("jaopca", "ftbic.plate_to_gear."+name),
								plateLocation, 4, gearLocation, 1, 1D);
					}
					else {
						addToMaterials = true;
					}
				}
			}
			if(type.isCrystalline() && !generalBlacklist.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(gearLocation)) {
					if(!addToFTBICMaterials) {
						helper.registerRollingRecipe(
								new ResourceLocation("jaopca", "ftbic.material_to_gear."+name),
								materialLocation, 2, gearLocation, 1, 1D);
					}
					else {
						addToMaterials = true;
					}
				}
			}
			if(!type.isDust() && !generalBlacklist.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				if(itemTags.contains(rodLocation)) {
					if(!addToFTBICMaterials) {
						helper.registerExtrudingRecipe(
								new ResourceLocation("jaopca", "ftbic.material_to_rod."+name),
								materialLocation, 1, rodLocation, 2, 1D);
					}
					else {
						addToMaterials = true;
					}
				}
			}
			if(addToMaterials) {
				helper.addMaterial(name);
			}
		}
	}
}
