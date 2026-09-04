package thelm.jaopca.compat.nuclearcraftneohaul;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
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

@JAOPCAModule(modDependencies = "nuclearcraft")
public class NuclearCraftNeohaulCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "beryllium", "boron", "boron_nitride", "carobbiite", "coal", "cobalt_oxide",
			"copper", "diamond", "dysprosium", "fluorite", "gadolinium", "graphite", "holmium", "iridium_oxide",
			"lead", "lithium", "magnesium", "manganese", "manganese_dioxide", "manganese_oxide", "molybdenum",
			"nickel_oxide", "obsidian", "palladium", "prismarine", "quartz", "rhodochrosite", "ruthenium_oxide",
			"silicon", "silver", "thorium", "tin", "tin_oxide", "uranium", "villiaumite", "zirconia", "zirconium"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(List.of(
			"boron_nitride", "carobbiite", "coal", "diamond", "emerald", "fluorite", "obsidian", "quartz", "rhodochrosite", "villiaumite"));
	private static final Set<String> MOLTEN_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "beryllium", "boron", "caesium_137", "carbboiite", "coal", "copper", "diamond",
			"dysprosium", "emerald", "europium_155", "ferroboron", "fluorite", "gadolinium", "gold", "hard_carbon",
			"hastelloy", "holmium", "iron", "lapis", "lead", "lead_platinium", "lithium", "magnesium", "manganese",
			"manganese_dioxide", "molybdenum", "nether_brick", "nickel_oxide", "obsidian", "palladium", "promethium_147",
			"purpur", "quartz", "redstone", "ruthenium_106", "silicon", "silicon_carbide", "silver", "steel", "strontium_90",
			"thorium", "tin", "tough", "uranium", "villiaumite", "zirconium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "nuclearcraft_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have manufactory to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pressurizer to crystal recipes added."),
				configToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material melter to molten recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust melter to molten recipes added."),
				configDustToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have ingot former to material recipes added."),
				configToMaterialBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		NuclearCraftNeohaulHelper helper = NuclearCraftNeohaulHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Set<ResourceLocation> fluidTags = api.getFluidTags();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			boolean isIngot = type.isIngot();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerManufactoryRecipe(
							miscHelper.getRecipeKey("nuclearcraftneohaul.material_to_dust", name),
							materialLocation, 1, dustLocation, 1, 0, 1, 1);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerPressurizerRecipe(
							miscHelper.getRecipeKey("nuclearcraftneohaul.dust_to_material", name),
							dustLocation, 1, materialLocation, 1, 0, 1, 1);
				}
			}
			if(!type.isDust()) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				int baseAmount = isIngot ? 90 : 100;
				if(fluidTags.contains(moltenLocation)) {
					if(!MOLTEN_BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerMelterRecipe(
								miscHelper.getRecipeKey("nuclearcraftneohaul.material_to_molten", name),
								materialLocation, 1, moltenLocation, baseAmount, 0, 1, 1);
					}
					if(!MOLTEN_BLACKLIST.contains(name) && !configDustToMoltenBlacklist.contains(name)) {
						ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
						if(itemTags.contains(dustLocation)) {
							helper.registerMelterRecipe(
									miscHelper.getRecipeKey("nuclearcraftneohaul.dust_to_molten", name),
									dustLocation, 1, moltenLocation, baseAmount, 0, 1, 1);
						}
					}
					if(!MOLTEN_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerIngotFormerRecipe(
								miscHelper.getRecipeKey("nuclearcraftneohaul.molten_to_material", name),
								moltenLocation, baseAmount, materialLocation, 1, 0, 1, 1);
					}
				}
			}
		}
	}
}
