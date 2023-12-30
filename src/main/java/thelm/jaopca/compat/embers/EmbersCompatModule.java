package thelm.jaopca.compat.embers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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

@JAOPCAModule(modDependencies = "embers")
public class EmbersCompatModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "bronze", "copper", "dawnstone", "electrum", "gold", "iron", "lead", "nickel", "silver", "tin"));
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configPlateToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "embers_compat";
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
				config.getDefinedStringList("recipes.storageBlockToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have storage block melting recipes added."),
				configStorageBlockToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate melting recipes added."),
				configPlateToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have ingot stamping recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget stamping recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate stamping recipes added."),
				configToPlateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		EmbersHelper helper = EmbersHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Set<ResourceLocation> fluidTags = api.getFluidTags();
		Item ingotStamp = ForgeRegistries.ITEMS.getValue(new ResourceLocation("embers:ingot_stamp"));
		Item nuggetStamp = ForgeRegistries.ITEMS.getValue(new ResourceLocation("embers:nugget_stamp"));
		Item plateStamp = ForgeRegistries.ITEMS.getValue(new ResourceLocation("embers:plate_stamp"));
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			boolean isIngot = type.isIngot();
			if(!type.isDust()) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", name, "_");
				int baseAmount = isIngot ? 90 : 100;
				if(fluidTags.contains(moltenLocation)) {
					if(!BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerMeltingRecipe(
								new ResourceLocation("jaopca", "embers.material_to_molten."+name),
								materialLocation, moltenLocation, baseAmount);
					}
					if(!BLACKLIST.contains(name) && !configStorageBlockToMoltenBlacklist.contains(name)) {
						ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
						if(itemTags.contains(storageBlockLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "embers.storage_block_to_molten."+name),
									storageBlockLocation, moltenLocation, baseAmount*9);
						}
					}
					if(!BLACKLIST.contains(name) && !configNuggetToMoltenBlacklist.contains(name)) {
						ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
						if(itemTags.contains(nuggetLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "embers.nugget_to_molten."+name),
									nuggetLocation, moltenLocation, (int)Math.floor(baseAmount/9F));
						}
					}
					if(!BLACKLIST.contains(name) && !configPlateToMoltenBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerMeltingRecipe(
									new ResourceLocation("jaopca", "embers.plate_to_molten."+name),
									plateLocation, moltenLocation, baseAmount);
						}
					}
					if(!BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
						helper.registerStampingRecipe(
								new ResourceLocation("jaopca", "embers.molten_to_material."+name),
								ingotStamp, moltenLocation, baseAmount, materialLocation, 1);
					}
					if(!BLACKLIST.contains(name) && !configToNuggetBlacklist.contains(name)) {
						ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", name);
						if(itemTags.contains(nuggetLocation)) {
							helper.registerStampingRecipe(
									new ResourceLocation("jaopca", "embers.molten_to_nugget."+name),
									nuggetStamp, moltenLocation, (int)Math.ceil(baseAmount/9F), nuggetLocation, 1);
						}
					}
					if(!BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", name);
						if(itemTags.contains(plateLocation)) {
							helper.registerStampingRecipe(
									new ResourceLocation("jaopca", "embers.molten_to_plate."+name),
									plateStamp, moltenLocation, baseAmount, plateLocation, 1);
						}
					}
				}
			}
		}
	}
}
