package thelm.jaopca.compat.immersiveengineering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

@JAOPCAModule(modDependencies = "immersiveengineering")
public class ImmersiveEngineeringCompatModule implements IModule {

	private static final Set<String> GENERAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "ardite", "brass", "bronze", "cobalt", "constantan", "copper", "electrum",
			"gold", "invar", "iron", "lead", "manyullyn", "nickel", "osmium", "platinum", "silver", "steel",
			"tin", "tungsten", "uranium", "zinc"));
	private static final Set<String> HAMMER_TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "constantan", "copper", "electrum", "gold", "iron", "lead", "nickel",
			"silver", "steel", "uranium"));
	private static final Set<String> WIRECUTTER_TO_WIRE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "copper", "electrum", "steel"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToIngotBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configToWireBlacklist = new TreeSet<>();
	private static Set<String> configHammerToPlateBlacklist = new TreeSet<>();
	private static Set<String> configWirecutterToWireBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "immersiveengineering_compat";
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
				config.getDefinedStringList("recipes.toIngotMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have arc furnace to ingot recipes added."),
				configToIngotBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to rod recipes added."),
				configToRodBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toWireMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to wire recipes added."),
				configToWireBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.hammerToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have hammering recipes added."),
				configHammerToPlateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ImmersiveEngineeringHelper helper = ImmersiveEngineeringHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Item gearMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:mold_gear"));
		Item plateMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:mold_plate"));
		Item rodMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:mold_rod"));
		Item wireMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:mold_wire"));
		Item hammer = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:hammer"));
		Item wirecutter = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:wirecutter"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !GENERAL_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerCrusherRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.material_to_dust."+name),
							materialLocation, new Object[] {
									dustLocation, 1,
							}, 3000);
				}
			}
			if(type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToIngotBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				if(itemTags.contains(dustLocation)) {
					helper.registerArcFurnaceRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.dust_to_material."+name),
							new Object[] {
									dustLocation, 1,
							}, new Object[] {
									materialLocation, 1,
							}, 100, 51200);
				}
			}
			if(type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(gearLocation)) {
					helper.registerMetalPressRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.material_to_gear."+name),
							materialLocation, 4, gearMold, gearLocation, 1, 2400);
				}
			}
			if(type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerMetalPressRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.material_to_plate_press."+name),
							materialLocation, 1, plateMold, plateLocation, 1, 2400);
				}
			}
			if(type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				if(itemTags.contains(rodLocation)) {
					helper.registerMetalPressRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.material_to_rod."+name),
							materialLocation, 1, rodMold, rodLocation, 2, 2400);
				}
			}
			if(type.isIngot() && !GENERAL_BLACKLIST.contains(name) && !configToWireBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation wireLocation = miscHelper.getTagLocation("wires", name);
				if(itemTags.contains(wireLocation)) {
					helper.registerMetalPressRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.material_to_wire."+name),
							materialLocation, 1, wireMold, wireLocation, 2, 2400);
				}
			}
			if(type.isIngot() && !HAMMER_TO_PLATE_BLACKLIST.contains(name) && !configHammerToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					api.registerShapelessRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.material_to_plate_hammer."+name),
							plateLocation, 1, new Object[] {
									materialLocation, hammer,
							});
				}
			}
			if(type.isIngot() && !WIRECUTTER_TO_WIRE_BLACKLIST.contains(name) && !configWirecutterToWireBlacklist.contains(name)) {
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				ResourceLocation wireLocation = miscHelper.getTagLocation("wires", name);
				if(itemTags.contains(plateLocation) && itemTags.contains(wireLocation)) {
					api.registerShapelessRecipe(
							new ResourceLocation("jaopca", "immersiveengineering.plate_to_wire."+name),
							wireLocation, 1, new Object[] {
									plateLocation, wirecutter,
							});
				}
			}
		}
	}
}
