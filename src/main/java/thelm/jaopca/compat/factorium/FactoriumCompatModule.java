package thelm.jaopca.compat.factorium;

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

@JAOPCAModule(modDependencies = "factorium")
public class FactoriumCompatModule implements IModule {

	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"brass", "bronze", "constantan", "copper", "electrum", "gold", "invar", "iron", "lead", "nickel",
			"platinum", "silver", "steel", "tin", "zinc"));
	private static final Set<String> TO_GEAR_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "copper", "electrum", "gold", "invar", "iron", "steel", "tin"));
	private static final Set<String> TO_ROD_BLACKLIST = new TreeSet<>(Arrays.asList(
			"brass", "bronze", "copper", "electrum", "gold", "invar", "iron", "steel", "tin"));
	private static final Set<String> TO_WIRE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "platinum"));
	private static final Set<String> TO_NUGGET_BLACKLIST = new TreeSet<>(Arrays.asList(
			"brass", "bronze", "constantan", "copper", "electrum", "gold", "invar", "iron", "lead", "nickel",
			"platinum", "silver", "steel", "tin", "zinc"));
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configToWireBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "factorium_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have extruder to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have extruder to gear recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have extruder to rod recipes added."),
				configToRodBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toWireMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have extruder to wire recipes added."),
				configToWireBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have extruder to nugget recipes added."),
				configToNuggetBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FactoriumHelper helper = FactoriumHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Item plateDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("factorium:die_plate"));
		Item gearDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("factorium:die_gear"));
		Item rodDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("factorium:die_rod"));
		Item wireDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("factorium:die_wire"));
		Item nuggetDie = ForgeRegistries.ITEMS.getValue(new ResourceLocation("factorium:die_nugget"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
				if(itemTags.contains(plateLocation)) {
					helper.registerExtruderRecipe(
							new ResourceLocation("jaopca", "factorium.material_to_plate."+name),
							materialLocation, 1, plateDie, plateLocation, 1);
				}
			}
			if(type.isIngot() && !TO_GEAR_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
				if(itemTags.contains(gearLocation)) {
					helper.registerExtruderRecipe(
							new ResourceLocation("jaopca", "factorium.material_to_gear."+name),
							materialLocation, 4, gearDie, gearLocation, 1);
				}
			}
			if(type.isIngot() && !TO_ROD_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
				if(itemTags.contains(rodLocation)) {
					helper.registerExtruderRecipe(
							new ResourceLocation("jaopca", "factorium.material_to_rod."+name),
							materialLocation, 1, rodDie, rodLocation, 2);
				}
			}
			if(type.isIngot() && !TO_WIRE_BLACKLIST.contains(name) && !configToWireBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation wireLocation = miscHelper.getTagLocation("wires", name);
				if(itemTags.contains(wireLocation)) {
					helper.registerExtruderRecipe(
							new ResourceLocation("jaopca", "factorium.material_to_wire."+name),
							materialLocation, 1, wireDie, wireLocation, 2);
				}
			}
			if(type.isIngot() && !TO_WIRE_BLACKLIST.contains(name) && !configToNuggetBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
				if(itemTags.contains(nuggetLocation)) {
					helper.registerExtruderRecipe(
							new ResourceLocation("jaopca", "factorium.material_to_nugget."+name),
							materialLocation, 1, nuggetDie, nuggetLocation, 9);
				}
			}
		}
	}
}
