package thelm.jaopca.compat.voluminousenergy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.Identifier;
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

@JAOPCAModule(modDependencies = "voluminousenergy")
public class VoluminousEnergyCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"coal", "lapis", "quartz"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "aluminium", "carbon", "solarium", "titanium"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "emerald", "lapis", "quartz"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "voluminousenergy_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have crusher to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have implosion compressor to material recipes added."),
				configToCrystalBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		VoluminousEnergyHelper helper = VoluminousEnergyHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<Identifier> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				Identifier dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerCrushingRecipe(
							miscHelper.getRecipeKey("voluminousenergy.material_to_dust", name),
							materialLocation, 1, dustLocation, 1, 200, 0, 0);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				Identifier plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerCompressingRecipe(
							miscHelper.getRecipeKey("voluminousenergy.material_to_plate", name),
							materialLocation, 1, plateLocation, 1, 200);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				Identifier dustLocation = miscHelper.getTagLocation("dusts", name);
				Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerImplosionCompressingRecipe(
							miscHelper.getRecipeKey("voluminousenergy.dust_to_material", name),
							dustLocation, 1, materialLocation, 1, 200);
				}
			}
		}
	}
}
