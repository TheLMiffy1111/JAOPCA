package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "mekanism")
public class MekanismCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"bronze", "charcoal", "coal", "copper", "diamond", "emerald", "fluorite", "gold", "iron", "lapis",
			"lead", "netherite", "osmium", "quartz", "refined_glowstone", "refined_obsidian", "steel", "tin",
			"uranium"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(List.of(
			"charcoal", "coal", "diamond", "emerald", "fluorite", "lapis", "quartz"));
	private static final Set<String> TO_ORE_BLACKLIST = new TreeSet<>(List.of(
			"coal", "copper", "diamond", "emerald", "fluorite", "gold", "iron", "lapis", "lead", "netherite",
			"netherite_scrap", "osmium", "quartz", "redstone", "tin", "uranium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configToOreBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("allthemodium")) {
			List<String> materials = List.of("allthemodium", "unobtainium", "vibranium");
			TO_DUST_BLACKLIST.addAll(materials);
			TO_ORE_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("alltheores")) {
			List<String> materials = List.of("aluminum", "aluminium", "nickel", "platinum", "silver", "zinc");
			TO_DUST_BLACKLIST.addAll(materials);
			TO_ORE_BLACKLIST.addAll(materials);
		}
	}

	@Override
	public String getName() {
		return "mekanism_compat";
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
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have enriching to material recipes added."),
				configToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toOreMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have combining to ore recipes added."),
				configToOreBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		ResourceLocation deepslateOreLocation = new ResourceLocation("forge:ores_in_ground/deepslate");
		ResourceLocation netherrackOreLocation = new ResourceLocation("forge:ores_in_ground/netherrack");
		ResourceLocation endstoneOreLocation = new ResourceLocation("forge:ores_in_ground/end_stone");
		ResourceLocation cobbledDeepslateLocation = new ResourceLocation("forge:cobblestone/deepslate");
		ResourceLocation netherrackLocation = new ResourceLocation("forge:netherrack");
		ResourceLocation endstoneLocation = new ResourceLocation("forge:end_stones");
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "mekanism.material_to_dust."+name),
							materialLocation, 1, dustLocation, 1);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerEnrichingRecipe(
							new ResourceLocation("jaopca", "mekanism.dust_to_material."+name),
							dustLocation, 1, materialLocation, 1);
				}
			}
			if(type.isOre() && !TO_ORE_BLACKLIST.contains(name) && !configToOreBlacklist.contains(name)) {
				ResourceLocation ingLocation = miscHelper.getTagLocation(type == MaterialType.INGOT ? "raw_materials" : "dusts", name);
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", name);
				if(itemTags.contains(ingLocation)) {
					IDynamicSpecConfig config = api.getMaterialConfig(material);
					String configOreBase = config.getDefinedString("mekanism.oreBase", "#forge:cobblestone/normal",
							this::isTagOrItemValid, "The default base to use in Mekanism's Combiner to recreate ores.");
					Object oreBase = getTagOrItem(configOreBase);

					int inputCount = switch(type) {
					case INGOT, INGOT_LEGACY, DUST -> inputCount = 8;
					case GEM, CRYSTAL -> 5; // I feel like 5 is more balanced
					default -> throw new IllegalArgumentException("Unexpected value: " + type);
					};

					helper.registerCombiningRecipe(
							new ResourceLocation("jaopca", "mekanism.material_to_default_ore."+name),
							ingLocation, inputCount, oreBase, 1,
							CompoundIngredientObject.difference(new Object[] {
									oreLocation,
									deepslateOreLocation, netherrackOreLocation, endstoneOreLocation,
							}), 1);
					helper.registerCombiningRecipe(
							new ResourceLocation("jaopca", "mekanism.material_to_deepslate_ore."+name),
							ingLocation, inputCount, cobbledDeepslateLocation, 1,
							CompoundIngredientObject.intersection(new Object[] {
									oreLocation, deepslateOreLocation,
							}), 1);
					helper.registerCombiningRecipe(
							new ResourceLocation("jaopca", "mekanism.material_to_netherrack_ore."+name),
							ingLocation, inputCount, netherrackLocation, 1,
							CompoundIngredientObject.intersection(new Object[] {
									oreLocation, netherrackOreLocation,
							}), 1);
					if(itemTags.contains(endstoneOreLocation)) {
						helper.registerCombiningRecipe(
								new ResourceLocation("jaopca", "mekanism.material_to_end_stone_ore."+name),
								ingLocation, inputCount, endstoneLocation, 1,
								CompoundIngredientObject.intersection(new Object[] {
										oreLocation, endstoneOreLocation,
								}), 1);
					}
				}
			}
		}
	}

	public boolean isTagOrItemValid(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return ApiImpl.INSTANCE.getItemTags().contains(new ResourceLocation(s.substring(1)));
		}
		else {
			return BuiltInRegistries.ITEM.containsKey(new ResourceLocation(s));
		}
	}

	public Object getTagOrItem(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return new ResourceLocation(s.substring(1));
		}
		else {
			return BuiltInRegistries.ITEM.get(new ResourceLocation(s));
		}
	}
}
