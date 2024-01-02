package thelm.jaopca.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "thermal_expansion@[1.4.0,)")
public class ThermalExpansionCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "diamond", "electrum", "emerald", "enderium", "gold", "invar", "iron",
			"lapis", "lead", "lumium", "nickel", "quartz", "ruby", "sapphire", "signalum", "silver", "tin"));
	private static final Set<String> to_material_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "enderium", "lead", "lumium", "nickel", "silver", "tin"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"));
	private static final Set<String> TO_GEAR_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"));
	private static final Set<String> MATERIAL_TO_COIN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"));
	private static final Set<String> NUGGET_TO_COIN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"));
	private static final Set<String> CHILLER_to_material_BLACKLIST = new TreeSet<>();
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToIngotBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToCoinBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToCoinBlacklist = new TreeSet<>();
	private static Set<String> configChillerToIngotBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("immersiveengineering")) {
			Collections.addAll(TO_PLATE_BLACKLIST, "uranium");
		}
		if(ModList.get().isLoaded("silents_mechanisms")) {
			Collections.addAll(TO_DUST_BLACKLIST, "brass");
			Collections.addAll(to_material_BLACKLIST, "brass");
		}
		if(ModList.get().isLoaded("uselessmod")) {
			Collections.addAll(TO_DUST_BLACKLIST, "super_useless", "useless");
			Collections.addAll(TO_PLATE_BLACKLIST, "super_useless", "useless");
			Collections.addAll(TO_GEAR_BLACKLIST, "super_useless", "useless");
		}
		if(ModList.get().isLoaded("tconstruct")) {
			Collections.addAll(CHILLER_to_material_BLACKLIST, "bronze", "cobalt", "constantan", "copper", "debris",
					"electrum", "gold", "hepatizon", "invar", "iron", "knightslime", "lead", "manyullyn", "netherite",
					"netherite_scrap", "nickel", "pig_iron", "queens_slime", "rose_gold", "silver", "silicon_bronze",
					"slimesteel", "tin");
		}
	}

	@Override
	public String getName() {
		return "thermal_expansion_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have pulverizer recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have smelter recipes added."),
				configToIngotBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToCoinMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press material to coin recipes added."),
				configMaterialToCoinBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToCoinMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press nugget to coin recipes added."),
				configNuggetToCoinBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.chillerToIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have chiller recipes added."),
				configChillerToIngotBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThermalExpansionHelper helper = ThermalExpansionHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Set<ResourceLocation> fluidTags = api.getFluidTags();
		Item gearDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_gear_die"));
		Item coinDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_coin_die"));
		Item ingotCast = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:chiller_ingot_cast"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(itemTags.contains(dustLocation)) {
					helper.registerPulverizerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_dust."+material.getName()),
							materialLocation, 1, new Object[] {
									dustLocation,
							}, 2000, 0F);
				}
			}
			if(type.isIngot() && !to_material_BLACKLIST.contains(name) && !configToIngotBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(itemTags.contains(dustLocation)) {
					helper.registerSmelterRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.dust_to_material."+material.getName()), new Object[] {
									dustLocation,
							}, new Object[] {
									materialLocation,
							}, 1600, 0F);
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(itemTags.contains(plateLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !TO_GEAR_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", material.getName());
				if(itemTags.contains(gearLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_gear."+material.getName()),
							materialLocation, 4, gearDie, 1, gearLocation, 1,
							2400, 0F);
				}
			}
			if(type.isIngot() && !MATERIAL_TO_COIN_BLACKLIST.contains(name) && !configMaterialToCoinBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
				if(itemTags.contains(coinLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_coin."+material.getName()),
							materialLocation, 1, coinDie, 1, coinLocation, 3,
							2400, 0F);
				}
			}
			if(type.isIngot() && !NUGGET_TO_COIN_BLACKLIST.contains(name) && !configNuggetToCoinBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
				if(itemTags.contains(nuggetLocation) && itemTags.contains(coinLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.nugget_to_coin."+material.getName()),
							nuggetLocation, 3, coinDie, 1, coinLocation, 1,
							800, 0F);
				}
			}
			if(type.isIngot() && !CHILLER_to_material_BLACKLIST.contains(name) && !configChillerToIngotBlacklist.contains(name)) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(fluidTags.contains(moltenLocation)) {
					helper.registerChillerRecipe(new ResourceLocation("jaopca", "thermal_expansion.molten_to_material."+material.getName()),
							moltenLocation, 144, ingotCast, 1, materialLocation, 1,
							5000, 0F);
				}
			}
		}
	}
}
