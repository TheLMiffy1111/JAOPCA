package thelm.jaopca.compat.createmetallurgy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

@JAOPCAModule(modDependencies = "createmetallurgy")
public class CreateMetallurgyCompatModule implements IModule {

	private static final Set<String> MATERIAL_BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "aluminium", "brass", "bronze", "constantan", "copper", "electrum", "gold", "invar",
			"iron", "lead", "netherite", "nickel", "osmium", "silver", "steel", "tin", "tungsten", "void_steel",
			"zinc"));
	private static final Set<String> DUST_BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "aluminium", "brass", "bronze", "constantan", "copper", "electrum", "gold", "invar",
			"iron", "lead", "netherite", "nickel", "osmium", "silver", "steel", "tin", "void_steel", "wolframite",
			"zinc"));
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDirtyDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configPlateToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configGearToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configCoinToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configRodToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configWireToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToStorageBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "createmetallurgy_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have ingot melting recipes added."),
				configMaterialToMoltenBlacklist);
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
				config.getDefinedStringList("recipes.wireToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have wire melting recipes added."),
				configRodToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have ingot casting recipes added."),
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
		CreateMetallurgyHelper helper = CreateMetallurgyHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Set<ResourceLocation> fluidTags = api.getFluidTags();
		Item ingotMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("createmetallurgy:graphite_ingot_mold"));
		Item nuggetMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("createmetallurgy:graphite_nugget_mold"));
		Item plateMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("createmetallurgy:graphite_plate_mold"));
		Item gearMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("createmetallurgy:graphite_gear_mold"));
		Item rodMold = ForgeRegistries.ITEMS.getValue(new ResourceLocation("createmetallurgy:graphite_rod_mold"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			boolean isIngot = type.isIngot();
			if(!type.isDust()) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				int baseAmount = isIngot ? 90 : 100;
				if(fluidTags.contains(moltenLocation)) {
					if(!MATERIAL_BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerMeltingRecipe(
								new ResourceLocation("jaopca", "createmetallurgy.material_to_molten."+name),
								materialLocation, moltenLocation, baseAmount, 40, 1);
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configNuggetToMoltenBlacklist.contains(name)) {
						ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
						if(itemTags.contains(nuggetLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.nugget_to_molten."+name),
									nuggetLocation, moltenLocation, (int)Math.floor(baseAmount/9F), 4, 1);
						}
					}
					if(!DUST_BLACKLIST.contains(name) && !configDustToMoltenBlacklist.contains(name)) {
						ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
						if(itemTags.contains(dustLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.dust_to_molten."+name),
									dustLocation, moltenLocation, baseAmount, 20, 1);
						}
					}
					if(!DUST_BLACKLIST.contains(name) && !configDirtyDustToMoltenBlacklist.contains(name)) {
						ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("dirty_dusts", name);
						if(itemTags.contains(dirtyDustLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.dirty_dust_to_molten."+name),
									dirtyDustLocation, moltenLocation, baseAmount, 35, 1);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configPlateToMoltenBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.plate_to_molten."+name),
									plateLocation, moltenLocation, baseAmount, 40, 1);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configGearToMoltenBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("gears", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.gear_to_molten."+name),
									plateLocation, moltenLocation, baseAmount*4, 160, 1);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configCoinToMoltenBlacklist.contains(name)) {
						ResourceLocation coinLocation = miscHelper.getTagLocation("coins", name);
						if(itemTags.contains(coinLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.coin_to_molten."+name),
									coinLocation, moltenLocation, (int)Math.floor(baseAmount/9F), 4, 1);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configRodToMoltenBlacklist.contains(name)) {
						ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
						if(itemTags.contains(rodLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.rod_to_molten."+name),
									rodLocation, moltenLocation, (int)Math.floor(baseAmount/2F), 4, 20);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerCastingTableRecipe(
								new ResourceLocation("jaopca", "createmetallurgy.molten_to_material."+name),
								ingotMold, moltenLocation, baseAmount,
								materialLocation, 1, 60, false);
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToStorageBlockBlacklist.contains(name)) {
						ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
						if(itemTags.contains(storageBlockLocation)) {
							helper.registerCastingBasinRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.molten_to_storage_block."+name),
									ItemStack.EMPTY, moltenLocation, baseAmount*(material.isSmallStorageBlock() ? 4 : 9),
									storageBlockLocation, 1, 320, false);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToNuggetBlacklist.contains(name)) {
						ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
						if(itemTags.contains(nuggetLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.molten_to_nugget."+name),
									nuggetMold, moltenLocation, (int)Math.ceil(baseAmount/9F),
									nuggetLocation, 1, 10, false);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.molten_to_plate."+name),
									plateMold, moltenLocation, baseAmount,
									plateLocation, 1, 60, false);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToGearBlacklist.contains(name)) {
						ResourceLocation gearLocation = miscHelper.getTagLocation("gears", name);
						if(itemTags.contains(gearLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.molten_to_gear."+name),
									gearMold, moltenLocation, baseAmount*4,
									gearLocation, 1, 160, false);
						}
					}
					if(!MATERIAL_BLACKLIST.contains(name) && !configToRodBlacklist.contains(name)) {
						ResourceLocation rodLocation = miscHelper.getTagLocation("rods", name);
						if(itemTags.contains(rodLocation)) {
							helper.registerCastingTableRecipe(
									new ResourceLocation("jaopca", "createmetallurgy.molten_to_rod."+name),
									rodMold, moltenLocation, (int)Math.ceil(baseAmount/2F),
									rodLocation, 1, 30, false);
						}
					}
				}
			}
		}
	}
}
