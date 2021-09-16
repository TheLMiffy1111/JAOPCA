package thelm.jaopca.compat.tconstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
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

@JAOPCAModule(modDependencies = "tconstruct")
public class TConstructCompatModule implements IModule {

	private static final Set<String> MATERIAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "brick", "bronze", "cobalt", "constantan", "copper", "debris",
			"diamond", "electrum", "emerald", "gold", "hepatizon", "invar", "iron", "knightslime", "lead",
			"manyullyn", "netherite", "netherite_scrap", "nickel", "osmium", "pewter", "pig_iron",
			"platinum", "quartz", "queens_slime", "rose_gold", "silicon_bronze", "silver", "slimesteel",
			"soulsteel", "steel", "tin", "tungsten", "uranium", "zinc"));
	private static final Set<String> STORAGE_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "bronze", "cobalt", "constantan", "copper", "diamond",
			"electrum", "emerald", "gold", "hepatizon", "invar", "iron", "knightslime", "lead",
			"manyullyn", "netherite", "nickel", "osmium", "pewter", "pig_iron", "platinum", "quartz",
			"queens_slime", "rose_gold", "silicon_bronze", "silver", "slimesteel", "soulsteel", "steel",
			"tin", "tungsten", "uranium", "zinc"));
	private static final Set<String> NUGGET_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "bronze", "cobalt", "constantan", "copper", "debris",
			"electrum", "gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn",
			"netherite", "netherite_scrap", "nickel", "osmium", "pewter", "pig_iron", "platinum",
			"queens_slime", "rose_gold", "silicon_bronze", "silver", "slimesteel", "soulsteel", "steel",
			"tin", "tungsten", "uranium", "zinc"));
	private static final Set<String> DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "bronze", "cobalt", "constantan", "copper", "electrum",
			"gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn", "netherite",
			"nickel", "osmium", "pewter", "pig_iron", "platinum", "queens_slime", "rose_gold",
			"silicon_bronze", "silver", "slimesteel", "soulsteel", "steel", "tin", "tungsten", "uranium",
			"zinc"));
	private static final Set<String> PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "brick", "bronze", "cobalt", "constantan", "copper",
			"electrum", "gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn",
			"netherite", "nickel", "osmium", "pewter", "pig_iron", "platinum", "queens_slime", "rose_gold",
			"silicon_bronze", "silver", "slimesteel", "soulsteel", "steel", "tin", "tungsten", "uranium",
			"zinc"));
	private static final Set<String> COIN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "bronze", "cobalt", "constantan", "copper", "electrum",
			"gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn", "netherite",
			"nickel", "osmium", "pewter", "pig_iron", "platinum", "queens_slime", "rose_gold",
			"silicon_bronze", "silver", "slimesteel", "soulsteel", "steel", "tin", "tungsten", "uranium",
			"zinc"));
	private static final Set<String> GENERAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "brass", "bronze", "cobalt", "constantan", "copper", "electrum",
			"gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn", "netherite",
			"nickel", "osmium", "pewter", "pig_iron", "platinum", "queens_slime", "rose_gold",
			"silicon_bronze", "silver", "slimesteel", "soulsteel", "steel", "tin", "tungsten", "uranium",
			"zinc"));
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configPlateToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configGearToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configCoinToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configRodToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configSheetmetalToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToCoinBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = true;

	static {
		if(ModList.get().isLoaded("allthemodium")) {
			List<String> materials = Arrays.asList("allthemodium", "unobtainium", "vibranium");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("materialis")) {
			List<String> materials = Arrays.asList("arcane_gold", "cloggrum", "ebony_psimetal", "fairy",
					"forgotten_metal", "froststeel", "iesnium", "ivory_psimetal", "neptunium", "pink_slime",
					"psimetal", "quicksilver", "refined_glowstone", "refined_obsidian", "refined_radiance",
					"regalium", "shadow_steel", "starmetal", "utherium");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			COIN_BLACKLIST.addAll(materials);
			GENERAL_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("bettercompat")) {
			Collections.addAll(MATERIAL_BLACKLIST, "aeternium", "amethyst", "black_opal", "certus_quartz",
					"elementium", "endorium", "fluix", "manasteel", "moonstone", "neptunium", "terminite",
					"terrasteel", "thallasium");
			Collections.addAll(STORAGE_BLOCK_BLACKLIST, "certus_quartz", "elementium", "endorium", "fluix",
					"manasteel", "moonstone", "neptunium", "terrasteel");
			Collections.addAll(NUGGET_BLACKLIST, "endorium", "terminite", "thallasium");
		}
		if(ModList.get().isLoaded("tdelight")) {
			List<String> materials = Arrays.asList("gildedfern", "hamletite", "rosenquartz");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			COIN_BLACKLIST.addAll(materials);
			GENERAL_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("natureminerals")) {
			List<String> materials = Arrays.asList("astrite", "kunzite", "stibnite", "thounite", "uvarovite");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
			PLATE_BLACKLIST.addAll(materials);
			GENERAL_BLACKLIST.addAll(materials);
		}
		if(ModList.get().isLoaded("taiga")) {
			List<String> materials = Arrays.asList("abyssum", "adamant", "astrium", "aurorium", "basalt",
					"dilithium", "duranite", "dyonite", "eezo", "fractum", "ignitz", "imperomite", "iox",
					"jauxum", "karmesine", "lumix", "meteorite", "nihilite", "niob", "nucleum", "obsidiorite",
					"osram", "ovium", "palladium", "prometheum", "proxii", "seismum", "solarium", "terrax",
					"tiberium", "triberium", "tritonite", "uru", "valyrium", "vibranium", "violium", "yrdeen");
			MATERIAL_BLACKLIST.addAll(materials);
			STORAGE_BLOCK_BLACKLIST.addAll(materials);
			NUGGET_BLACKLIST.addAll(materials);
			DUST_BLACKLIST.addAll(materials);
		}
	}

	@Override
	public String getName() {
		return "tconstruct_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");

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
				config.getDefinedStringList("recipes.coinToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have coin melting recipes added."),
				configCoinToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.rodToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rod melting recipes added."),
				configRodToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.sheetmetalToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have sheetmetal melting recipes added."),
				configSheetmetalToMoltenBlacklist);
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
				config.getDefinedStringList("recipes.toCoinMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have coin casting recipes added."),
				configToCoinBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rod casting recipes added."),
				configToRodBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getAttributes().getTemperature(stack)-300;
		ToIntFunction<FluidStack> castTimeFunction = stack->ICastingRecipe.calcCoolingTime(stack);
		ResourceLocation ingotCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/ingot");
		ResourceLocation ingotCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/ingot");
		ResourceLocation gemCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/gem");
		ResourceLocation gemCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/gem");
		ResourceLocation nuggetCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/nugget");
		ResourceLocation nuggetCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/nugget");
		ResourceLocation plateCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/plate");
		ResourceLocation plateCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/plate");
		ResourceLocation gearCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/gear");
		ResourceLocation gearCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/gear");
		ResourceLocation coinCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/coin");
		ResourceLocation coinCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/coin");
		ResourceLocation rodCastMultiLocation = new ResourceLocation("tconstruct:casts/multi_use/rod");
		ResourceLocation rodCastSingleLocation = new ResourceLocation("tconstruct:casts/single_use/rod");
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					(!jaopcaOnly || moltenMaterials.contains(material))) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				if(api.getFluidTags().contains(moltenLocation)) {
					if(!MATERIAL_BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
						helper.registerMeltingRecipe(
								new ResourceLocation("jaopca", "tconstruct.material_to_molten."+material.getName()),
								materialLocation, moltenLocation, 144,
								tempFunction, getMeltTimeFunction(1F), false);
					}
					if(!STORAGE_BLOCK_BLACKLIST.contains(name) && !configStorageBlockToMoltenBlacklist.contains(name)) {
						ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
						if(api.getItemTags().contains(storageBlockLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.storage_block_to_molten."+material.getName()),
									storageBlockLocation, moltenLocation, 1296,
									tempFunction, getMeltTimeFunction(3F), false);
						}
					}
					if(!NUGGET_BLACKLIST.contains(name) && !configNuggetToMoltenBlacklist.contains(name)) {
						ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
						if(api.getItemTags().contains(nuggetLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.nugget_to_molten."+material.getName()),
									nuggetLocation, moltenLocation, 16,
									tempFunction, getMeltTimeFunction(1/3F), false);
						}
					}
					if(!DUST_BLACKLIST.contains(name) && !configDustToMoltenBlacklist.contains(name)) {
						ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
						if(api.getItemTags().contains(dustLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.nugget_to_molten."+material.getName()),
									dustLocation, moltenLocation, 144,
									tempFunction, getMeltTimeFunction(0.75F), false);
						}
					}
					if(!PLATE_BLACKLIST.contains(name) && !configPlateToMoltenBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
						if(api.getItemTags().contains(plateLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.plate_to_molten."+material.getName()),
									plateLocation, moltenLocation, 144,
									tempFunction, getMeltTimeFunction(1F), false);
						}
					}
					if(!GENERAL_BLACKLIST.contains(name) && !configGearToMoltenBlacklist.contains(name)) {
						ResourceLocation gearLocation = miscHelper.getTagLocation("gears", material.getName());
						if(api.getItemTags().contains(gearLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.gear_to_molten."+material.getName()),
									gearLocation, moltenLocation, 576,
									tempFunction, getMeltTimeFunction(2F), false);
						}
					}
					if(!COIN_BLACKLIST.contains(name) && !configCoinToMoltenBlacklist.contains(name)) {
						ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
						if(api.getItemTags().contains(coinLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.coin_to_molten."+material.getName()),
									coinLocation, moltenLocation, 48,
									tempFunction, getMeltTimeFunction(2/3F), false);
						}
					}
					if(!GENERAL_BLACKLIST.contains(name) && !configRodToMoltenBlacklist.contains(name)) {
						ResourceLocation rodLocation = miscHelper.getTagLocation("rods", material.getName());
						if(api.getItemTags().contains(rodLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.rod_to_molten."+material.getName()),
									rodLocation, moltenLocation, 72,
									tempFunction, getMeltTimeFunction(0.2F), false);
						}
					}
					if(!GENERAL_BLACKLIST.contains(name) && !configSheetmetalToMoltenBlacklist.contains(name)) {
						ResourceLocation sheetmetalLocation = miscHelper.getTagLocation("sheetmetals", material.getName());
						if(api.getItemTags().contains(sheetmetalLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.sheetmetal_to_molten."+material.getName()),
									sheetmetalLocation, moltenLocation, 144,
									tempFunction, getMeltTimeFunction(1F), false);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
						if(ArrayUtils.contains(MaterialType.INGOTS, type)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_material_gold_cast."+material.getName()),
									ingotCastMultiLocation, moltenLocation, 144,
									materialLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_material_sand_cast."+material.getName()),
									ingotCastSingleLocation, moltenLocation, 144,
									materialLocation, 1,
									castTimeFunction, true, false);
						}
						else {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_material_gold_cast."+material.getName()),
									gemCastMultiLocation, moltenLocation, 144,
									materialLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_material_sand_cast."+material.getName()),
									gemCastSingleLocation, moltenLocation, 144,
									materialLocation, 1,
									castTimeFunction, true, false);
						}
					}
					if(!STORAGE_BLOCK_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
						ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
						if(api.getItemTags().contains(storageBlockLocation)) {
							helper.registerCastingBasinRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_storage_block."+material.getName()),
									Ingredient.EMPTY, moltenLocation, 1296,
									storageBlockLocation, 1,
									castTimeFunction, false, false);
						}
					}
					if(!NUGGET_BLACKLIST.contains(name) && !configToNuggetBlacklist.contains(name)) {
						ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
						if(api.getItemTags().contains(nuggetLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_nugget_gold_cast."+material.getName()),
									nuggetCastMultiLocation, moltenLocation, 16,
									nuggetLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_nugget_sand_cast."+material.getName()),
									nuggetCastSingleLocation, moltenLocation, 16,
									nuggetLocation, 1,
									castTimeFunction, true, false);
						}
					}
					if(!PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
						if(api.getItemTags().contains(plateLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_plate_gold_cast."+material.getName()),
									plateCastMultiLocation, moltenLocation, 144,
									plateLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_plate_sand_cast."+material.getName()),
									plateCastSingleLocation, moltenLocation, 144,
									plateLocation, 1,
									castTimeFunction, true, false);
						}
					}
					if(!GENERAL_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
						ResourceLocation gearLocation = miscHelper.getTagLocation("gears", material.getName());
						if(api.getItemTags().contains(gearLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_gear_gold_cast."+material.getName()),
									gearCastMultiLocation, moltenLocation, 576,
									gearLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_gear_sand_cast."+material.getName()),
									gearCastSingleLocation, moltenLocation, 576,
									gearLocation, 1,
									castTimeFunction, true, false);
						}
					}
					if(!GENERAL_BLACKLIST.contains(name) && !configToCoinBlacklist.contains(name)) {
						ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
						if(api.getItemTags().contains(coinLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_coin_gold_cast."+material.getName()),
									coinCastMultiLocation, moltenLocation, 48,
									coinLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_coin_sand_cast."+material.getName()),
									coinCastSingleLocation, moltenLocation, 48,
									coinLocation, 1,
									castTimeFunction, true, false);
						}
					}
					if(!GENERAL_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
						ResourceLocation rodLocation = miscHelper.getTagLocation("rods", material.getName());
						if(api.getItemTags().contains(rodLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_rod_gold_cast."+material.getName()),
									rodCastMultiLocation, moltenLocation, 72,
									rodLocation, 1,
									castTimeFunction, false, false);
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "tconstruct.molten_to_rod_sand_cast."+material.getName()),
									rodCastSingleLocation, moltenLocation, 72,
									rodLocation, 1,
									castTimeFunction, true, false);
						}
					}
				}
			}
		}
	}

	public ToIntFunction<FluidStack> getMeltTimeFunction(float timeFactor) {
		return stack->IMeltingRecipe.calcTime(stack.getFluid().getAttributes().getTemperature(stack)-300, timeFactor);
	}
}
