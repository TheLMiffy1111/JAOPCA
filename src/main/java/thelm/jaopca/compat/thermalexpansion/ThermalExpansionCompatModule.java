package thelm.jaopca.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

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

@JAOPCAModule(modDependencies = "thermal_expansion@[1.3.0,)")
public class ThermalExpansionCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "diamond", "electrum", "emerald", "enderium", "gold", "invar", "iron",
			"lapis", "lead", "lumium", "nickel", "quartz", "ruby", "sapphire", "signalum", "silver", "tin"
			));
	private static final Set<String> TO_INGOT_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "enderium", "lead", "lumium", "nickel", "silver", "tin"
			));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"
			));
	private static final Set<String> TO_GEAR_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"
			));
	private static final Set<String> TO_COIN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar", "iron", "lead", "lumium",
			"nickel", "signalum", "silver", "tin"
			));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToIngotBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToCoinBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("immersiveengineering")) {
			Collections.addAll(TO_PLATE_BLACKLIST, "uranium");
		}
		if(ModList.get().isLoaded("silents_mechanisms")) {
			Collections.addAll(TO_DUST_BLACKLIST, "brass");
			Collections.addAll(TO_INGOT_BLACKLIST, "brass");
		}
		if(ModList.get().isLoaded("uselessmod")) {
			Collections.addAll(TO_DUST_BLACKLIST, "super_useless", "useless");
			Collections.addAll(TO_PLATE_BLACKLIST, "super_useless", "useless");
			Collections.addAll(TO_GEAR_BLACKLIST, "super_useless", "useless");
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
				config.getDefinedStringList("recipes.toCoinMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to coin recipes added."),
				configToCoinBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThermalExpansionHelper helper = ThermalExpansionHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Item richSlag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:rich_slag"));
		Item gearDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_gear_die"));
		Item coinDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:press_coin_die"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerPulverizerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.material_to_dust."+material.getName()),
							materialLocation, 1, new Object[] {
									dustLocation,
							}, 2000, 0F);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_INGOT_BLACKLIST.contains(name) && !configToIngotBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerSmelterRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.dust_to_material."+material.getName()), new Object[] {
									dustLocation,
							}, new Object[] {
									materialLocation,
							}, 1600, 0F);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1,
							2400, 0F);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_GEAR_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", material.getName());
				if(api.getItemTags().contains(gearLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_gear."+material.getName()),
							materialLocation, 3, gearDie, 1, gearLocation, 1,
							2400, 0F);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_COIN_BLACKLIST.contains(name) && !configToCoinBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
				ResourceLocation coinLocation = miscHelper.getTagLocation("coins", material.getName());
				if(api.getItemTags().contains(coinLocation)) {
					helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.material_to_coin."+material.getName()),
							materialLocation, 1, coinDie, 1, coinLocation, 3,
							2400, 0F);
					if(api.getItemTags().contains(nuggetLocation)) {
						helper.registerPressRecipe(new ResourceLocation("jaopca", "thermal_expansion.nugget_to_coin."+material.getName()),
								nuggetLocation, 3, coinDie, 1, coinLocation, 1,
								800, 0F);
					}
				}
			}
		}
	}
}
