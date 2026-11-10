package thelm.jaopca.compat.hephaestus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
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

@JAOPCAModule(modDependencies = "hephaestus")
public class HephaestusCompatModule implements IModule {

	private static final Set<String> MATERIAL_BLACKLIST = new TreeSet<>(List.of(
			"amethyst", "cobalt", "copper", "diamond", "emerald", "gold", "iron", "lapis", "netherite",
			"netherite_scrap", "quartz", "steel"));
	private static final Set<String> STORAGE_BLOCK_BLACKLIST = new TreeSet<>(List.of(
			"amethyst", "cobalt", "copper", "diamond", "emerald", "gold", "iron", "lapis", "netherite",
			"netherite_scrap", "quartz", "steel"));
	private static final Set<String> NUGGET_BLACKLIST = new TreeSet<>(List.of(
			"cobalt", "copper", "gold", "iron", "netherite", "netherite_scrap", "steel"));
	private static final Set<String> DUST_BLACKLIST = new TreeSet<>(List.of("cobalt", "steel"));
	private static final Set<String> PLATE_BLACKLIST = new TreeSet<>();
	private static final Set<String> GEAR_BLACKLIST = new TreeSet<>();
	private static final Set<String> ROD_BLACKLIST = new TreeSet<>();
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configPlateToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configGearToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configRodToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("ae2")) {
			Collections.addAll(MATERIAL_BLACKLIST, "certus_quartz", "fluix");
		}
		if(ModList.get().isLoaded("allthemodium")) {
			List<String> materials = List.of("allthemodium", "unobtainium", "vibranium");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			GEAR_BLACKLIST.addAll(materials);
			ROD_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("alltheores")) {
			List<String> materials = List.of("aluminium", "aluminum", "brass", "bronze", "constantan", "electrum",
					"enderium", "invar", "iriddium", "lead", "lumium", "nickel", "osmium", "platinum", "signalum",
					"silver", "tin", "uranium", "zinc");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			GEAR_BLACKLIST.addAll(materials);
			ROD_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("arsmagicalegacy")) {
			List<String> materials = List.of("moonstone", "topaz");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("cakesticklib")) {
			List<String> materials = List.of("wrought_iron");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("colossal_reactors")) {
			List<String> materials = List.of("tough_alloy");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("enderio")) {
			List<String> materials = List.of("conductive_alloy", "dark_steel", "end_steel", "energetic_alloy",
					"pulsating_alloy", "redstone_alloy", "soularium", "vibrant_alloy");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("enderio_evolution")) {
			List<String> materials = List.of("energetic_silver", "vivid_alloy");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("energizedpower")) {
			Collections.addAll(MATERIAL_BLACKLIST, "advanced_alloy", "crystallized_alloy", "energized_alloy",
					"energized_copper", "energized_gold");
			Collections.addAll(STORAGE_BLOCK_BLACKLIST, "energized_alloy");
		}
		if(ModList.get().isLoaded("evilcraft")) {
			List<String> materials = List.of("dark");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("extendedae")) {
			Collections.addAll(MATERIAL_BLACKLIST, "entro");
		}
		if(ModList.get().isLoaded("ftbarmory")) {
			List<String> materials = List.of("adamantite", "aeternium", "aurichalcum");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			GEAR_BLACKLIST.addAll(materials);
			ROD_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("ftbic")) {
			List<String> materials = List.of("enderium");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			GEAR_BLACKLIST.addAll(materials);
			ROD_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("ftbmaterials")) {
			List<String> materials = List.of("aluminium", "aluminum", "brass", "bronze", "chromium", "constantan",
					"copper", "electrum", "gold", "invar", "iridium", "iron", "lead", "netherite", "nickel",
					"platinum", "silver", "stainless_steel", "tin", "titanium", "uranium", "zinc");
			List<String> gemMaterials = List.of("dimensional_shard", "ruby", "sapphire");
			MATERIAL_BLACKLIST.addAll(materials);
			MATERIAL_BLACKLIST.addAll(gemMaterials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(gemMaterials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			GEAR_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("irons_jewelry")) {
			Collections.addAll(MATERIAL_BLACKLIST, "garnet", "onyx", "peridot");
		}
		if(ModList.get().isLoaded("justdirethings")) {
			Collections.addAll(MATERIAL_BLACKLIST, "blazegold", "eclipsealloy", "ferricore");
		}
		if(ModList.get().isLoaded("ltxi")) {
			Collections.addAll(MATERIAL_BLACKLIST, "niobium", "olivine", "slatesteel", "tungsten_slatesteel");
			Collections.addAll(STORAGE_BLOCK_BLACKLIST, "niobium", "slatesteel");
			Collections.addAll(NUGGET_BLACKLIST, "niobium", "slatesteel");
		}
		if(ModList.get().isLoaded("mekanism")) {
			List<String> materials = List.of("refined_glowstone", "refined_obsidian");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("mysticalagradditions")) {
			Collections.addAll(MATERIAL_BLACKLIST, "awakened_supremium", "imperium", "insanium", "prosperity",
					"prudentium", "soulium", "supremium", "tertium");
		}
		if(ModList.get().isLoaded("neovitae")) {
			List<String> materials = List.of("hellforged");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("nodeworks")) {
			Collections.addAll(MATERIAL_BLACKLIST, "celestine");
		}
		if(ModList.get().isLoaded("occultism")) {
			Collections.addAll(MATERIAL_BLACKLIST, "spirit_attuned", "iesnium");
			Collections.addAll(STORAGE_BLOCK_BLACKLIST, "spirit_attuned", "iesnium");
			Collections.addAll(NUGGET_BLACKLIST, "iesnium");
		}
		if(ModList.get().isLoaded("oritech")) {
			Collections.addAll(MATERIAL_BLACKLIST, "adamant", "duratium", "fluxite", "prometheum");
			Collections.addAll(STORAGE_BLOCK_BLACKLIST, "adamant", "duratium", "fluxite");
		}
		if(ModList.get().isLoaded("replication")) {
			List<String> materials = List.of("replica");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("stellaris")) {
			List<String> materials = List.of("desh");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("thaumaturge")) {
			List<String> materials = List.of("thaumium", "void_metal");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("xycraft_world")) {
			Collections.addAll(MATERIAL_BLACKLIST, "xychorium");
		}
	}

	@Override
	public String getName() {
		return "hephaestus_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have ingot/gem melting recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.storageBlockToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have storage block melting recipes added."),
				configStorageBlockToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust melting recipes added."),
				configDustToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate melting recipes added."),
				configPlateToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.gearToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear melting recipes added."),
				configGearToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.rodToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rod melting recipes added."),
				configRodToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have ingot/gem casting recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have storage block casting recipes added."),
				configToStorageBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget casting recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate casting recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear casting recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rod casting recipes added."),
				configToRodBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		HephaestusHelper helper = HephaestusHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<Identifier> itemTags = api.getItemTags();
		Set<Identifier> fluidTags = api.getFluidTags();
		ToIntFunction<FluidStackTemplate> tempFunction = stack->stack.fluid().value().getFluidType().getTemperature()-300;
		Item ingotCast = BuiltInRegistries.ITEM.getValue(Identifier.parse("hephaestus:ingot_cast"));
		Item gemCast = BuiltInRegistries.ITEM.getValue(Identifier.parse("hephaestus:gem_cast"));
		Item nuggetCast = BuiltInRegistries.ITEM.getValue(Identifier.parse("hephaestus:nugget_cast"));
		Item plateCast = BuiltInRegistries.ITEM.getValue(Identifier.parse("hephaestus:plate_cast"));
		Item gearCast = BuiltInRegistries.ITEM.getValue(Identifier.parse("hephaestus:gear_cast"));
		Item rodCast = BuiltInRegistries.ITEM.getValue(Identifier.parse("hephaestus:rod_cast"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			boolean isIngot = type.isIngot();
			if(!type.isDust()) {
				Identifier moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				int baseAmount = isIngot ? 90 : 100;
				if(fluidTags.contains(moltenLocation)) {
					if(!MATERIAL_BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
						Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerMeltingRecipe(
								miscHelper.getRecipeKey("hephaestus.material_to_molten", name),
								materialLocation, null, 50, moltenLocation, baseAmount,
								tempFunction, (isIngot ? 100 : 120));
					}
					if(!STORAGE_BLOCK_BLACKLIST.contains(name) && !configStorageBlockToMoltenBlacklist.contains(name)) {
						Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerMeltingRecipe(
								miscHelper.getRecipeKey("hephaestus.storage_block_to_molten", name),
								materialLocation, null, 50, moltenLocation, baseAmount*(material.isSmallStorageBlock() ? 4 : 9),
								tempFunction, (isIngot ? 200 : 240));
					}
					if(!NUGGET_BLACKLIST.contains(name) && !configNuggetToMoltenBlacklist.contains(name)) {
						Identifier nuggetLocation = miscHelper.getTagLocation("nuggets", name);
						if(itemTags.contains(nuggetLocation)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("hephaestus.nugget_to_molten", name),
									nuggetLocation, null, 50, moltenLocation, (int)Math.floor(baseAmount/9F),
									tempFunction, (isIngot ? 33 : 40));
						}
					}
					if(!DUST_BLACKLIST.contains(name) && !configDustToMoltenBlacklist.contains(name)) {
						Identifier dustLocation = miscHelper.getTagLocation("dusts", name);
						if(itemTags.contains(dustLocation)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("hephaestus.dust_to_molten", name),
									dustLocation, null, 50, moltenLocation, baseAmount,
									tempFunction, (isIngot ? 100 : 120));
						}
					}
					if(!PLATE_BLACKLIST.contains(name) && !configPlateToMoltenBlacklist.contains(name)) {
						Identifier plateLocation = miscHelper.getTagLocation("plates", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("hephaestus.plate_to_molten", name),
									plateLocation, null, 50, moltenLocation, baseAmount,
									tempFunction, (isIngot ? 100 : 120));
						}
					}
					if(!GEAR_BLACKLIST.contains(name) && !configGearToMoltenBlacklist.contains(name)) {
						Identifier gearLocation = miscHelper.getTagLocation("gears", name);
						if(itemTags.contains(gearLocation)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("hephaestus.gear_to_molten", name),
									gearLocation, null, 50, moltenLocation, baseAmount*4,
									tempFunction, (isIngot ? 200 : 240));
						}
					}
					if(!ROD_BLACKLIST.contains(name) && !configRodToMoltenBlacklist.contains(name)) {
						Identifier rodLocation = miscHelper.getTagLocation("rods", name);
						if(itemTags.contains(rodLocation)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("hephaestus.gear_to_molten", name),
									rodLocation, null, 50, moltenLocation, (int)Math.floor(baseAmount/2F),
									tempFunction, (isIngot ? 50 : 60));
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
						Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerCastingTableRecipe(
								miscHelper.getRecipeKey("hephaestus.molten_to_material", name),
								(isIngot ? ingotCast : gemCast), moltenLocation, baseAmount, materialLocation, 1,
								(isIngot ? 100 : 120), false);
					}
					if(!STORAGE_BLOCK_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
						Identifier storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
						if(itemTags.contains(storageBlockLocation)) {
							helper.registerCastingBasinRecipe(
									miscHelper.getRecipeKey("hephaestus.molten_to_storage_block", name),
									null, moltenLocation, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), storageBlockLocation, 1,
									(isIngot ? 200 : 240));
						}
					}
					if(!NUGGET_BLACKLIST.contains(name) && !configToNuggetBlacklist.contains(name)) {
						Identifier nuggetLocation = miscHelper.getTagLocation("nuggets", name);
						if(itemTags.contains(nuggetLocation)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("hephaestus.molten_to_nugget", name),
									nuggetCast, moltenLocation, (int)Math.ceil(baseAmount/9F), nuggetLocation, 1,
									(isIngot ? 33 : 40), false);
						}
					}
					if(!PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
						Identifier plateLocation = miscHelper.getTagLocation("plates", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("hephaestus.molten_to_plate", name),
									plateCast, moltenLocation, baseAmount, plateLocation, 1,
									(isIngot ? 100 : 120), false);
						}
					}
					if(!GEAR_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
						Identifier gearLocation = miscHelper.getTagLocation("gears", name);
						if(itemTags.contains(gearLocation)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("hephaestus.molten_to_gear", name),
									gearCast, moltenLocation, baseAmount, gearLocation, 1,
									(isIngot ? 200 : 240), false);
						}
					}
					if(!ROD_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
						Identifier rodLocation = miscHelper.getTagLocation("rods", name);
						if(itemTags.contains(rodLocation)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("hephaestus.molten_to_rod", name),
									rodCast, moltenLocation, (int)Math.ceil(baseAmount/2F), rodLocation, 1,
									(isIngot ? 50 : 60), false);
						}
					}
				}
			}
		}
	}
}
