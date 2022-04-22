package thelm.jaopca.compat.electrodynamics;

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

@JAOPCAModule(modDependencies = "electrodynamics")
public class ElectrodynamicsCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "chromium", "copper", "gold", "iron", "lead", "lithium", "molybdenum", "netherite",
			"netherite_scrap", "silver", "steel", "superconductive", "tin", "vanadium"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "bronze", "hslasteel", "iron", "lithium", "stainlesssteel", "steel", "titanium",
			"titaniumcarbide", "vanadiumsteel"));
	private static final Set<String> TO_ROD_BLACKLIST = new TreeSet<>(Arrays.asList(
			"hslasteel", "stainlesssteel", "steel", "titaniumcarbide"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "electrodynamics_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have crusher to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have lathe to rod recipes added."),
				configToRodBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ElectrodynamicsHelper helper = ElectrodynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerMineralGrinderRecipe(
							new ResourceLocation("jaopca", "electrodynamics.material_to_dust."+material.getName()),
							materialLocation, 1, dustLocation, 1, 0.1);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerMineralCrusherRecipe(
							new ResourceLocation("jaopca", "electrodynamics.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1, 0.1);
				}
			}
			if(type.isIngot() && !TO_ROD_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", material.getName());
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
				if(api.getItemTags().contains(rodLocation)) {
					if(api.getItemTags().contains(nuggetLocation)) {
						helper.registerLatheRecipe(
								new ResourceLocation("jaopca", "electrodynamics.material_to_rod."+material.getName()),
								materialLocation, 2, rodLocation, 1, nuggetLocation, 2, 1, 0.1);
					}
					else {
						helper.registerLatheRecipe(
								new ResourceLocation("jaopca", "electrodynamics.material_to_rod."+material.getName()),
								materialLocation, 2, rodLocation, 1, 0.1);
					}
				}
			}
		}
	}
}
