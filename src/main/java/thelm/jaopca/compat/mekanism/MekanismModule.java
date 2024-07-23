package thelm.jaopca.compat.mekanism;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormType;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryInfo;
import thelm.jaopca.compat.mekanism.slurries.SlurryFormType;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "mekanism")
public class MekanismModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "lead", "netherite", "netherite_scrap", "osmium", "tin", "uranium"));

	static {
		if(ModList.get().isLoaded("allthemodium")) {
			Collections.addAll(BLACKLIST, "allthemodium", "unobtainium", "vibranium");
		}
		if(ModList.get().isLoaded("alltheores")) {
			Collections.addAll(BLACKLIST, "aluminum", "aluminium", "nickel", "platinum", "silver", "zinc");
		}
	}

	public MekanismModule() {
		SlurryFormType.init();
	}

	private final IForm dirtyDustForm = ApiImpl.INSTANCE.newForm(this, "mekanism_dirty_dusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("mekanism:dirty_dusts").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm clumpForm = ApiImpl.INSTANCE.newForm(this, "mekanism_clumps", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("mekanism:clumps").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm shardForm = ApiImpl.INSTANCE.newForm(this, "mekanism_shards", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("mekanism:shards").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "mekanism_crystals", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("mekanism:crystals").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm cleanSlurryForm = ApiImpl.INSTANCE.newForm(this, "mekanism_clean", SlurryFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("mekanism:clean").setDefaultMaterialBlacklist(BLACKLIST).
			setSkipGroupedCheck(true).setSettings(SlurryFormType.INSTANCE.getNewSettings().
					setOreTagFunction(material->MiscHelper.INSTANCE.getTagLocation("ores", material.getName()).toString()));
	private final IForm dirtySlurryForm = ApiImpl.INSTANCE.newForm(this, "mekanism_dirty", SlurryFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("mekanism:dirty").setDefaultMaterialBlacklist(BLACKLIST).
			setSkipGroupedCheck(true).setSettings(SlurryFormType.INSTANCE.getNewSettings().
					setOreTagFunction(material->MiscHelper.INSTANCE.getTagLocation("ores", material.getName()).toString()));
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			dirtyDustForm, clumpForm, shardForm, crystalForm, dirtySlurryForm, cleanSlurryForm).setGrouped(true);

	@Override
	public String getName() {
		return "mekanism";
	}

	@Override
	public void defineModuleConfigPre(IModuleData moduleData, IDynamicSpecConfig config) {
		MekanismDataInjector.setupConfig(config);
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(formRequest);
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		ISlurryFormType slurryFormType = SlurryFormType.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		ResourceLocation waterLocation = ResourceLocation.parse("minecraft:water");
		for(IMaterial material : formRequest.getMaterials()) {
			ISlurryInfo dirtySlurryInfo = slurryFormType.getMaterialFormInfo(dirtySlurryForm, material);
			ResourceLocation dirtySlurryLocation = miscHelper.getTagLocation("mekanism:dirty", material.getName());
			ISlurryInfo cleanSlurryInfo = slurryFormType.getMaterialFormInfo(cleanSlurryForm, material);
			ResourceLocation cleanSlurryLocation = miscHelper.getTagLocation("mekanism:clean", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			ResourceLocation crystalLocation = miscHelper.getTagLocation("mekanism:crystals", material.getName());
			IItemInfo shardInfo = itemFormType.getMaterialFormInfo(shardForm, material);
			ResourceLocation shardLocation = miscHelper.getTagLocation("mekanism:shards", material.getName());
			IItemInfo clumpInfo = itemFormType.getMaterialFormInfo(clumpForm, material);
			ResourceLocation clumpLocation = miscHelper.getTagLocation("mekanism:clumps", material.getName());
			IItemInfo dirtyDustInfo = itemFormType.getMaterialFormInfo(dirtyDustForm, material);
			ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("mekanism:dirty_dusts", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			helper.registerDissolutionRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_dirty_slurry", material.getName()),
					oreLocation, 1, MekanismGases.SULFURIC_ACID, 1, dirtySlurryInfo, 1000);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerDissolutionRecipe(
						miscHelper.getRecipeKey("mekanism.raw_material_to_dirty_slurry", material.getName()),
						rawMaterialLocation, 3, MekanismGases.SULFURIC_ACID, 1, dirtySlurryInfo, 2000);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerDissolutionRecipe(
							miscHelper.getRecipeKey("mekanism.raw_storage_block_to_dirty_slurry", material.getName()),
							rawStorageBlockLocation, 1, MekanismGases.SULFURIC_ACID, 2, dirtySlurryInfo, 6000);
				}
			}

			helper.registerWashingRecipe(
					miscHelper.getRecipeKey("mekanism.dirty_to_clean_slurry", material.getName()),
					waterLocation, 5, dirtySlurryLocation, 1, cleanSlurryInfo, 1);

			helper.registerCrystallizingRecipe(
					miscHelper.getRecipeKey("mekanism.clean_slurry_to_crystal", material.getName()),
					cleanSlurryLocation, 200, crystalInfo, 1);

			helper.registerInjectingRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_shard", material.getName()),
					oreLocation, 1, MekanismGases.HYDROGEN_CHLORIDE, 1, shardInfo, 4);
			helper.registerInjectingRecipe(
					miscHelper.getRecipeKey("mekanism.crystal_to_shard", material.getName()),
					crystalLocation, 1, MekanismGases.HYDROGEN_CHLORIDE, 1, shardInfo, 1);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerInjectingRecipe(
						miscHelper.getRecipeKey("mekanism.raw_material_to_shard", material.getName()),
						rawMaterialLocation, 3, MekanismGases.HYDROGEN_CHLORIDE, 1, shardInfo, 8);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerInjectingRecipe(
							miscHelper.getRecipeKey("mekanism.raw_storage_block_to_shard", material.getName()),
							rawStorageBlockLocation, 1, MekanismGases.HYDROGEN_CHLORIDE, 2, shardInfo, 24);
				}
			}

			helper.registerPurifyingRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_clump", material.getName()),
					oreLocation, 1, MekanismGases.OXYGEN, 1, clumpInfo, 3);
			helper.registerPurifyingRecipe(
					miscHelper.getRecipeKey("mekanism.shard_to_clump", material.getName()),
					shardLocation, 1, MekanismGases.OXYGEN, 1, clumpInfo, 1);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerPurifyingRecipe(
						miscHelper.getRecipeKey("mekanism.raw_material_to_clump", material.getName()),
						rawMaterialLocation, 1, MekanismGases.OXYGEN, 1, clumpInfo, 2);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerPurifyingRecipe(
							miscHelper.getRecipeKey("mekanism.raw_storage_block_to_clump", material.getName()),
							rawStorageBlockLocation, 1, MekanismGases.OXYGEN, 2, clumpInfo, 18);
				}
			}

			helper.registerCrushingRecipe(
					miscHelper.getRecipeKey("mekanism.clump_to_dirty_dust", material.getName()),
					clumpLocation, 1, dirtyDustInfo, 1);

			helper.registerEnrichingRecipe(
					miscHelper.getRecipeKey("mekanism.dirty_dust_to_dust", material.getName()),
					dirtyDustLocation, 1, dustLocation, 1);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerEnrichingRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_dust", material.getName()),
					oreLocation, 1, dustLocation, 2);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				helper.registerEnrichingRecipe(
						miscHelper.getRecipeKey("mekanism.raw_material_to_dust", material.getName()),
						rawMaterialLocation, 3, dustLocation, 4);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerEnrichingRecipe(
							miscHelper.getRecipeKey("mekanism.raw_storage_block_to_dust", material.getName()),
							rawStorageBlockLocation, 1, dustLocation, 12);
				}
			}
		}
	}
}
