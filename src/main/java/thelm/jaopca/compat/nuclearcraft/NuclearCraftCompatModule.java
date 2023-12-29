package thelm.jaopca.compat.nuclearcraft;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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

@JAOPCAModule(modDependencies = "nuclearcraft")
public class NuclearCraftCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "beryllium", "boron", "boron_arsenide", "boron_nitride", "bronze", "calcium",
			"carbon_manganese", "carobbite", "charcoal", "chromium", "coal", "cobalt", "copper", "diamond",
			"electrum", "extreme", "ferroboron", "fluorite", "gold", "graphite", "hafnium", "hard_carbon",
			"hsla_steel", "iron", "lapis", "lead", "lead_platinum", "lithium", "lithium_manganese_dioxide",
			"magnesium", "manganese", "manganese_dioxide", "manganese_oxide", "netherite", "niobium", "obsidian",
			"osmium", "palladium", "platinum", "potassium", "pyrolitic_carbon", "rhodochrosite", "shibuichi",
			"sic_sic_cmc", "silicon_carbide", "silver", "sodium", "steel", "strontium", "thermoconducting", "thorium",
			"tin", "tin_silver", "titanium", "tough_alloy", "tungsten", "uranium", "villiaumite", "yttrium", "zinc",
			"zircaloy", "zirconium_molybdenum", "zirconium"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "beryllium", "boron", "bronze", "cobalt", "electrum", "extreme", "ferroboron",
			"graphite", "hard_carbon", "hsla_steel", "lead", "lithium", "lithium_manganese_dioxide", "magnesium",
			"manganese", "netherite", "palladium", "platinum", "sic_sic_cmc", "silver", "steel", "thermoconducting",
			"thorium", "tin", "tough_alloy", "uranium", "zinc", "zirconium"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(List.of(
			"boron_nitride", "carobbiite", "diamond", "fluorite", "obsidian", "quartz", "rhodochrosite", "villiaumite"));
	private static final Set<String> MOLTEN_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "arsenic", "beryllium", "boron", "boron_arsenide", "bronze", "caesium_137",
			"carbon_manganese", "cobalt", "copper", "corium", "electrum", "enderium", "europium_155", "extreme",
			"ferroboron", "gold", "hard_carbon", "hsla_steel", "iron", "lapis", "lead", "lead_platinum",
			"lithium", "lithium_manganese_dioxide", "magnesium", "magnesium_diboride", "manganese",
			"manganese_dioxide", "manganese_oxide", "molybdenum", "obsidian", "palladium", "platinum", "polonium",
			"potassium_hydroxide", "potassium_iodide", "promethium_147", "purpur", "ruthenium_106", "shibuichi",
			"sic_sic_cmc", "silicon_carbide", "silver", "sodium_hydroxide", "steel", "strontium_90", "sulfur",
			"thermoconducting", "thorium", "tin", "tin_silver", "tough_alloy", "uranium", "zinc", "zircaloy",
			"zirconium_molybdenum", "zirconium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
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
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pressurizer to plate recipes added."),
				configToPlateBlacklist);
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
		NuclearCraftHelper helper = NuclearCraftHelper.INSTANCE;
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
							new ResourceLocation("jaopca", "nuclearcraft.material_to_dust."+name),
							materialLocation, 1, dustLocation, 1, 1, 1, 1);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerPressurizerRecipe(
							new ResourceLocation("jaopca", "nuclearcraft.material_to_plate."+name),
							materialLocation, 1, plateLocation, 1, 1, 1, 1);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerPressurizerRecipe(
							new ResourceLocation("jaopca", "nuclearcraft.dust_to_material."+name),
							dustLocation, 1, materialLocation, 1, 1, 1, 1);
				}
			}
			if(!type.isDust()) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				int baseAmount = isIngot ? 90 : 100;
				if(fluidTags.contains(moltenLocation)) {
					if(!MOLTEN_BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerMelterRecipe(
								new ResourceLocation("jaopca", "nuclearcraft.material_to_molten."+name),
								materialLocation, 1, moltenLocation, baseAmount, 1, 1, 1);
					}
					if(!MOLTEN_BLACKLIST.contains(name) && !configDustToMoltenBlacklist.contains(name)) {
						ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
						if(itemTags.contains(dustLocation)) {
							helper.registerMelterRecipe(
									new ResourceLocation("jaopca", "nuclearcraft.dust_to_molten."+name),
									dustLocation, 1, moltenLocation, baseAmount, 1, 1, 1);
						}
					}
					if(!MOLTEN_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerIngotFormerRecipe(
								new ResourceLocation("jaopca", "nuclearcraft.molten_to_material."+name),
								moltenLocation, baseAmount, materialLocation, 1, 1, 1, 1);
					}
				}
			}
		}
	}
}
