package thelm.jaopca.compat.embers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.Item;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import teamroots.embers.RegistryManager;
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

@JAOPCAModule(modDependencies = "embers@[1,)")
public class EmbersCompatModule implements IModule {

	public static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Bronze", "Copper", "Dawnstone", "Electrum", "Gold", "Iron", "Lead", "Nickel",
			"Silver", "Tin"));
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configPlateToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = false;

	@Override
	public String getName() {
		return "embers_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material melting recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate melting recipes added."),
				configPlateToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material stamping recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate stamping recipes added."),
				configToPlateBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		EmbersHelper helper = EmbersHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		Item ingotStamp = RegistryManager.stamp_bar;
		Item plateStamp = RegistryManager.stamp_plate;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName("", name);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configMaterialToMoltenBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerMeltingRecipe(
								miscHelper.getRecipeKey("embers.material_to_molten", name),
								materialOredict, moltenName, 144);
					}
					if(!configNuggetToMoltenBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("embers.nugget_to_molten", name),
									nuggetOredict, moltenName, 16);
						}
					}
					if(!configPlateToMoltenBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("embers.plate_to_molten", name),
									plateOredict, moltenName, 144);
						}
					}
					if(!configToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerStampingRecipe(
								miscHelper.getRecipeKey("embers.molten_to_material", name),
								null, moltenName, 144, ingotStamp, materialOredict, 1);
					}
					if(!configToPlateBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerStampingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_plate", name),
									null, moltenName, 144, plateStamp, plateOredict, 1);
						}
					}
				}
			}
		}
	}
}
