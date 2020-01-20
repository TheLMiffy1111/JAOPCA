package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import mekanism.common.registries.MekanismGases;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
import thelm.jaopca.api.resources.IInMemoryResourcePack;
import thelm.jaopca.compat.mekanism.api.gases.IGasInfo;
import thelm.jaopca.compat.mekanism.gases.GasFormType;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "mekanism")
public class MekanismModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "osmium", "tin"));

	public MekanismModule() {
		GasFormType.init();
	}

	private final IForm dirtyDustForm = ApiImpl.INSTANCE.newForm(this, "mekanism_dirty_dusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("mekanism:dirty_dusts").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm clumpForm = ApiImpl.INSTANCE.newForm(this, "mekanism_clumps", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("mekanism:clumps").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm shardForm = ApiImpl.INSTANCE.newForm(this, "mekanism_shards", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("mekanism:shards").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "mekanism_crystals", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("mekanism:crystals").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm dirtySlurryForm = ApiImpl.INSTANCE.newForm(this, "mekanism_dirty_slurry", GasFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("mekanism:dirty_slurry").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(GasFormType.INSTANCE.getNewSettings().setIsHidden(true));
	private final IForm cleanSlurryForm = ApiImpl.INSTANCE.newForm(this, "mekanism_clean_slurry", GasFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("mekanism:clean_slurry").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(GasFormType.INSTANCE.getNewSettings().setIsHidden(true));

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
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this,
				dirtyDustForm, clumpForm, shardForm, crystalForm, cleanSlurryForm, dirtySlurryForm));
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onMaterialComputeComplete(IModuleData moduleData) {
		GasFormType.registerEntries();
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : dirtySlurryForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			IGasInfo dirtySlurryInfo = GasFormType.INSTANCE.getMaterialFormInfo(dirtySlurryForm, material);
			helper.registerDissolutionRecipe(
					new ResourceLocation("jaopca", "mekanism.ore_to_dirty_slurry."+material.getName()),
					oreLocation, 1, MekanismGases.SULFURIC_ACID, 1, dirtySlurryInfo, 1000);
		}
		for(IMaterial material : cleanSlurryForm.getMaterials()) {
			ResourceLocation dirtySlurryLocation = miscHelper.getTagLocation("mekanism:dirty_slurry", material.getName());
			IGasInfo cleanSlurryInfo = GasFormType.INSTANCE.getMaterialFormInfo(cleanSlurryForm, material);
			helper.registerWashingRecipe(
					new ResourceLocation("jaopca", "mekanism.dirty_to_clean_slurry."+material.getName()),
					Fluids.WATER, 5, dirtySlurryLocation, 1, cleanSlurryInfo, 1);
		}
		for(IMaterial material : crystalForm.getMaterials()) {
			ResourceLocation cleanSlurryLocation = miscHelper.getTagLocation("mekanism:clean_slurry", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			helper.registerCrystallizingRecipe(
					new ResourceLocation("jaopca", "mekanism.clean_slurry_to_crystal."+material.getName()),
					cleanSlurryLocation, 200, crystalInfo, 1);
		}
		for(IMaterial material : shardForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation crystalLocation = miscHelper.getTagLocation("mekanism:crystals", material.getName());
			IItemInfo shardInfo = itemFormType.getMaterialFormInfo(shardForm, material);
			helper.registerInjectingRecipe(
					new ResourceLocation("jaopca", "mekanism.ore_to_shard."+material.getName()),
					oreLocation, 1, MekanismGases.HYDROGEN_CHLORIDE, 1, shardInfo, 4);
			helper.registerInjectingRecipe(
					new ResourceLocation("jaopca", "mekanism.crystal_to_shard."+material.getName()),
					crystalLocation, 1, MekanismGases.HYDROGEN_CHLORIDE, 1, shardInfo, 1);
		}
		for(IMaterial material : clumpForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation shardLocation = miscHelper.getTagLocation("mekanism:shards", material.getName());
			IItemInfo clumpInfo = itemFormType.getMaterialFormInfo(clumpForm, material);
			helper.registerPurifyingRecipe(
					new ResourceLocation("jaopca", "mekanism.ore_to_clump."+material.getName()),
					oreLocation, 1, MekanismGases.OXYGEN, 1, clumpInfo, 3);
			helper.registerPurifyingRecipe(
					new ResourceLocation("jaopca", "mekanism.shard_to_clump."+material.getName()),
					shardLocation, 1, MekanismGases.OXYGEN, 1, clumpInfo, 1);
		}
		for(IMaterial material : dirtyDustForm.getMaterials()) {
			ResourceLocation clumpLocation = miscHelper.getTagLocation("mekanism:clumps", material.getName());
			IItemInfo dirtyDustInfo = itemFormType.getMaterialFormInfo(dirtyDustForm, material);
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "mekanism.clump_to_dirty_dust."+material.getName()),
					clumpLocation, 1, dirtyDustInfo, 1);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("mekanism:dirty_dusts", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerEnrichingRecipe(
					new ResourceLocation("jaopca", "mekanism.ore_to_dust."+material.getName()),
					oreLocation, 1, dustLocation, 2);
			helper.registerEnrichingRecipe(
					new ResourceLocation("jaopca", "mekanism.dirty_dust_to_dust."+material.getName()),
					dirtyDustLocation, 1, dustLocation, 1);
		}
	}

	@Override
	public void onCreateDataPack(IModuleData moduleData, IInMemoryResourcePack resourcePack) {
		MekanismDataInjector.putJsons(resourcePack);
	}
}
