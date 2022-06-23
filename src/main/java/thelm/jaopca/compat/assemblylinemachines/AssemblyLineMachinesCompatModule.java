package thelm.jaopca.compat.assemblylinemachines;

import java.util.ArrayList;
import java.util.Arrays;
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

@JAOPCAModule(modDependencies = "assemblylinemachines")
public class AssemblyLineMachinesCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"chromium", "coal", "copper", "diamond", "flerovium", "gold", "iron", "lapis", "netherite",
			"netherite_scrap", "titanium"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"attuned_titanium", "chromium", "copper", "energized_gold", "flerovium", "gold", "iron", "mystium",
			"novasteel", "steel", "titanium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
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
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have metal shaper to plate recipes added."),
				configToPlateBlacklist);
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
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerGrinderRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_dust."+name),
							materialLocation, dustLocation, 1, 5, 2, false, 0);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerMetalRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.material_to_plate_metal."+name),
							materialLocation, plateLocation, 1, 6);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configHammerToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				ResourceLocation hammerLocation = new ResourceLocation("assemblylinemachines:hammers");
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
