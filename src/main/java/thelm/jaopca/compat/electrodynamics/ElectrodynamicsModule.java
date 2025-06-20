package thelm.jaopca.compat.electrodynamics;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
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
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "electrodynamics@[0.10.1,)")
public class ElectrodynamicsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "aluminium", "chromium", "copper", "gold", "iron", "lead", "lithium", "molybdenum",
			"netherite", "netherite_scrap", "silver", "tin", "titanium", "vanadium"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IFluidFormSettings solutionSettings = FluidFormType.INSTANCE.getNewSettings().
			setFallDistanceModifierFunction(material->0).setCanExtinguishFunction(material->true).
			setSupportsBoatingFunction(material->true). setCanHydrateFunction(material->true);

	private final IForm impureDustForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_impuredusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:impuredusts").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_crystals", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:crystals").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm pureMineralForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_pureminerals", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:pureminerals").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(solutionSettings);
	private final IForm sulfateForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_sulfates", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:sulfates").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(solutionSettings);
	private final IForm impureMineralForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_impureminerals", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:impureminerals").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(solutionSettings);
	private final IForm dirtyMineralForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_dirtyminerals", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:dirtyminerals").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(solutionSettings);
	private final IForm crudeMineralForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_crudeminerals", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:crudeminerals").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(solutionSettings);
	private final IForm royalMineralForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_royalminerals", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:royalminerals").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(solutionSettings);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			impureDustForm, crystalForm, pureMineralForm, sulfateForm, impureMineralForm, dirtyMineralForm, crudeMineralForm, royalMineralForm).setGrouped(true);

	@Override
	public String getName() {
		return "electrodynamics";
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
	public void onMaterialConfigAvailable(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		ElectrodynamicsHelper helper = ElectrodynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		IFluidFormType fluidFormType = FluidFormType.INSTANCE;
		ResourceLocation aquaRegiaLocation = ResourceLocation.parse("c:aquaregia");
		ResourceLocation sulfuricAcidLocation = ResourceLocation.parse("c:sulfuric_acid");
		ResourceLocation waterLocation = ResourceLocation.parse("minecraft:water");
		Fluid aquaRegia = BuiltInRegistries.FLUID.get(ResourceLocation.parse("electrodynamics:fluidaquaregia"));
		Fluid sulfuricAcid = BuiltInRegistries.FLUID.get(ResourceLocation.parse("electrodynamics:fluidsulfuricacid"));
		for(IMaterial material : formRequest.getMaterials()) {
			IFluidInfo royalMineralInfo = fluidFormType.getMaterialFormInfo(royalMineralForm, material);
			ResourceLocation royalMineralLocation = miscHelper.getTagLocation("electrodynamics:royalminerals", material.getName());
			IFluidInfo crudeMineralInfo = fluidFormType.getMaterialFormInfo(crudeMineralForm, material);
			ResourceLocation crudeMineralLocation = miscHelper.getTagLocation("electrodynamics:crudeminerals", material.getName());
			IFluidInfo dirtyMineralInfo = fluidFormType.getMaterialFormInfo(dirtyMineralForm, material);
			ResourceLocation dirtyMineralLocation = miscHelper.getTagLocation("electrodynamics:dirtyminerals", material.getName());
			IFluidInfo impureMineralInfo = fluidFormType.getMaterialFormInfo(impureMineralForm, material);
			ResourceLocation impureMineralLocation = miscHelper.getTagLocation("electrodynamics:impureminerals", material.getName());
			IFluidInfo sulfateInfo = fluidFormType.getMaterialFormInfo(sulfateForm, material);
			ResourceLocation sulfateLocation = miscHelper.getTagLocation("electrodynamics:sulfates", material.getName());
			IFluidInfo pureMineralInfo = fluidFormType.getMaterialFormInfo(pureMineralForm, material);
			ResourceLocation pureMineralLocation = miscHelper.getTagLocation("electrodynamics:pureminerals", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			ResourceLocation crystalLocation = miscHelper.getTagLocation("electrodynamics:crystals", material.getName());
			IItemInfo impureDustInfo = itemFormType.getMaterialFormInfo(impureDustForm, material);
			ResourceLocation impureDustLocation = miscHelper.getTagLocation("electrodynamics:impuredusts", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			helper.registerMineralWasherRecipe(
					miscHelper.getRecipeKey("electrodynamics.ore_to_royal_mineral", material.getName()),
					oreLocation, 1, aquaRegiaLocation, 1000, royalMineralInfo, 4000, 0, 200, 400);

			helper.registerChemicalReactorRecipe(
					miscHelper.getRecipeKey("electrodynamics.royal_mineral_to_crude_mineral", material.getName()),
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							royalMineralLocation, 200,
							waterLocation, 1000,
					}, ObjectArrays.EMPTY_ARRAY,
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							crudeMineralInfo, 200,
							aquaRegia, 50, 1F,
					}, ObjectArrays.EMPTY_ARRAY,
					0, 100, 800);

			helper.registerChemicalReactorRecipe(
					miscHelper.getRecipeKey("electrodynamics.crude_mineral_to_dirty_mineral", material.getName()),
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							crudeMineralLocation, 200,
							sulfuricAcidLocation, 500,
					}, ObjectArrays.EMPTY_ARRAY,
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							dirtyMineralInfo, 200,
							Fluids.WATER, 200, 0.25F,
					}, ObjectArrays.EMPTY_ARRAY,
					0, 100, 600);

			helper.registerChemicalReactorRecipe(
					miscHelper.getRecipeKey("electrodynamics.dirty_mineral_to_impure_mineral", material.getName()),
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							dirtyMineralLocation, 200,
							waterLocation, 1000,
					}, ObjectArrays.EMPTY_ARRAY,
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							impureMineralInfo, 200,
							sulfuricAcid, 500, 1F,
					}, ObjectArrays.EMPTY_ARRAY,
					0, 100, 700);
			helper.registerMineralWasherRecipe(
					miscHelper.getRecipeKey("electrodynamics.ore_to_sulfate", material.getName()),
					oreLocation, 1, sulfuricAcidLocation, 1000, sulfateInfo, 1000, 0, 200, 400);

			helper.registerChemicalReactorRecipe(
					miscHelper.getRecipeKey("electrodynamics.sulfate_to_pure_mineral", material.getName()),
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							sulfateLocation, 200,
							waterLocation, 1000,
					}, ObjectArrays.EMPTY_ARRAY,
					ObjectArrays.EMPTY_ARRAY, new Object[] {
							pureMineralInfo, 200,
							sulfuricAcid, 150, 1F,
					}, ObjectArrays.EMPTY_ARRAY,
					0, 200, 800);

			helper.registerElectrolosisChamberRecipe(
					miscHelper.getRecipeKey("electrodynamics.impure_mineral_to_pure_material", material.getName()),
					impureMineralLocation, 1, pureMineralInfo, 1, 0, 0, 0);

			helper.registerChemicalCrystallizerRecipe(
					miscHelper.getRecipeKey("electrodynamics.pure_mineral_to_crystal", material.getName()),
					pureMineralLocation, 200, crystalInfo, 1, 0, 200, 800);

			helper.registerMineralCrusherRecipe(
					miscHelper.getRecipeKey("electrodynamics.ore_to_impure_dust", material.getName()),
					oreLocation, 1, impureDustInfo, 3, 0.3, 200, 450);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerMineralCrusherRecipe(
						miscHelper.getRecipeKey("electrodynamics.raw_material_to_impure_dust", material.getName()),
						rawMaterialLocation, 1, impureDustInfo, 3, 0.3, 200, 450);
			}
			helper.registerMineralCrusherRecipe(
					miscHelper.getRecipeKey("electrodynamics.crystal_to_impure_dust", material.getName()),
					crystalLocation, 1, impureDustInfo, 1, 0, 200, 450);

			helper.registerMineralGrinderRecipe(
					miscHelper.getRecipeKey("electrodynamics.impure_dust_to_dust", material.getName()),
					impureDustLocation, 1, dustLocation, 1, 0.1, 200, 350);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("electrodynamics.byproduct", "minecraft:cobblestone",
					s->BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(s)), "The byproduct material to output in Create's crushing.");
			Item byproduct = BuiltInRegistries.ITEM.get(ResourceLocation.parse(configByproduct));

			helper.registerMineralGrinderRecipe(
					miscHelper.getRecipeKey("electrodynamics.ore_to_dust", material.getName()),
					oreLocation, 1, dustLocation, 2, byproduct, 1, 0.1, 0.3, 200, 350);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerMineralGrinderRecipe(
						miscHelper.getRecipeKey("electrodynamics.raw_material_to_dust", material.getName()),
						rawMaterialLocation, 1, dustLocation, 2, 0.1, 200, 350);
			}
		}
	}
}
