package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import mekanism.common.MekanismFluids;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
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
import thelm.jaopca.compat.mekanism.api.gases.IGasFormType;
import thelm.jaopca.compat.mekanism.api.gases.IGasInfo;
import thelm.jaopca.compat.mekanism.gases.GasFormType;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "mekanism")
public class MekanismModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Copper", "Gold", "Iron", "Lead", "Osmium", "Silver", "Tin"));
	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Draconium", "Iridium", "Mithril", "Nickel", "Uranium", "Yellorium"));

	public MekanismModule() {
		GasFormType.init();
	}

	private final IForm dirtyDustForm = ApiImpl.INSTANCE.newForm(this, "mekanism_dirty_dust", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("dustDirty").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm clumpForm = ApiImpl.INSTANCE.newForm(this, "mekanism_clump", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("clump").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm shardForm = ApiImpl.INSTANCE.newForm(this, "mekanism_shard", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("shard").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "mekanism_crystal", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystal").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm cleanSlurryForm = ApiImpl.INSTANCE.newForm(this, "mekanism_clean_slurry", GasFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("clean_slurry").setDefaultMaterialBlacklist(BLACKLIST).
			setSkipGroupedCheck(true);
	private final IForm dirtySlurryForm = ApiImpl.INSTANCE.newForm(this, "mekanism_slurry", GasFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("slurry").setDefaultMaterialBlacklist(BLACKLIST).
			setSkipGroupedCheck(true);

	@Override
	public String getName() {
		return "mekanism";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this,
				dirtyDustForm, clumpForm, shardForm, crystalForm, dirtySlurryForm, cleanSlurryForm).setGrouped(true));
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
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		IGasFormType gasFormType = GasFormType.INSTANCE;
		for(IMaterial material : dirtySlurryForm.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			IGasInfo dirtySlurryInfo = gasFormType.getMaterialFormInfo(dirtySlurryForm, material);
			helper.registerChemicalDissolutionChamberRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_dirty_slurry", material.getName()),
					oreOredict, 1, dirtySlurryInfo, 1000);
		}
		for(IMaterial material : cleanSlurryForm.getMaterials()) {
			String dirtySlurryName = miscHelper.getFluidName("slurry", material.getName());
			IGasInfo cleanSlurryInfo = gasFormType.getMaterialFormInfo(cleanSlurryForm, material);
			helper.registerChemicalWasherRecipe(
					miscHelper.getRecipeKey("mekanism.dirty_to_clean_slurry", material.getName()),
					dirtySlurryName, 1, cleanSlurryInfo, 1);
		}
		for(IMaterial material : crystalForm.getMaterials()) {
			String cleanSlurryName = miscHelper.getFluidName("clean_slurry", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			helper.registerChemicalCrystallizerRecipe(
					miscHelper.getRecipeKey("mekanism.clean_slurry_to_crystal", material.getName()),
					cleanSlurryName, 200, crystalInfo, 1);
		}
		for(IMaterial material : shardForm.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String crystalOredict = miscHelper.getOredictName("crystal", material.getName());
			IItemInfo shardInfo = itemFormType.getMaterialFormInfo(shardForm, material);
			helper.registerChemicalInjectionChamberRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_shard", material.getName()),
					oreOredict, 1, MekanismFluids.HydrogenChloride, shardInfo, 4);
			helper.registerChemicalInjectionChamberRecipe(
					miscHelper.getRecipeKey("mekanism.crystal_to_shard", material.getName()),
					crystalOredict, 1, MekanismFluids.HydrogenChloride, shardInfo, 1);
		}
		for(IMaterial material : clumpForm.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String shardOredict = miscHelper.getOredictName("shard", material.getName());
			IItemInfo clumpInfo = itemFormType.getMaterialFormInfo(clumpForm, material);
			helper.registerPurificationChamberRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_clump", material.getName()),
					oreOredict, 1, clumpInfo, 3);
			helper.registerPurificationChamberRecipe(
					miscHelper.getRecipeKey("mekanism.shard_to_clump", material.getName()),
					shardOredict, 1, clumpInfo, 1);
		}
		for(IMaterial material : dirtyDustForm.getMaterials()) {
			String clumpOredict = miscHelper.getOredictName("clump", material.getName());
			IItemInfo dirtyDustInfo = itemFormType.getMaterialFormInfo(dirtyDustForm, material);
			helper.registerCrusherRecipe(
					miscHelper.getRecipeKey("mekanism.clump_to_dirty_dust", material.getName()),
					clumpOredict, 1, dirtyDustInfo, 1);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dirtyDustOredict = miscHelper.getOredictName("dustDirty", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			helper.registerEnrichmentChamberRecipe(
					miscHelper.getRecipeKey("mekanism.ore_to_dust", material.getName()),
					oreOredict, 1, dustOredict, 2);
			helper.registerEnrichmentChamberRecipe(
					miscHelper.getRecipeKey("mekanism.dirty_dust_to_dust", material.getName()),
					dirtyDustOredict, 1, dustOredict, 1);
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("dustdirty", "mekanism_dirty_dust");
		builder.put("clump", "mekanism_clump");
		builder.put("shard", "mekanism_shard");
		builder.put("crystal", "mekanism_crystal");
		builder.put("slurryclean", "mekanism_clean_slurry");
		builder.put("slurry", "mekanism_slurry");
		return builder.build();
	}
}
