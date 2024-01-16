package thelm.jaopca.compat.electrodynamics;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.config.IDynamicSpecConfig;
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

@JAOPCAModule(modDependencies = "electrodynamics")
public class ElectrodynamicsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "aluminium", "chromium", "copper", "gold", "iron", "lead", "lithium", "molybdenum",
			"netherite", "netherite_scrap", "silver", "tin", "titanium", "vanadium"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm impureDustForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_impuredusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:impuredusts").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_crystals", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:crystals").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm sulfateForm = ApiImpl.INSTANCE.newForm(this, "electrodynamics_sulfates", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("electrodynamics:sulfates").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			impureDustForm, crystalForm, sulfateForm).setGrouped(true);

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
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		ElectrodynamicsHelper helper = ElectrodynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		IFluidFormType fluidFormType = FluidFormType.INSTANCE;
		ResourceLocation sulfuricAcidLocation = new ResourceLocation("forge:sulfuric_acid");
		Item sulfurTrioxide = ForgeRegistries.ITEMS.getValue(new ResourceLocation("electrodynamics:oxidetrisulfur"));
		for(IMaterial material : formRequest.getMaterials()) {
			IFluidInfo sulfateInfo = fluidFormType.getMaterialFormInfo(sulfateForm, material);
			ResourceLocation sulfateLocation = miscHelper.getTagLocation("electrodynamics:sulfates", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			ResourceLocation crystalLocation = miscHelper.getTagLocation("electrodynamics:crystals", material.getName());
			IItemInfo impureDustInfo = itemFormType.getMaterialFormInfo(impureDustForm, material);
			ResourceLocation impureDustLocation = miscHelper.getTagLocation("electrodynamics:impuredusts", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			//if(material.getType() == MaterialType.INGOT) {
			//	helper.registerMineralWasherRecipe(
			//			new ResourceLocation("jaopca", "electrodynamics.raw_material_to_sulfate."+material.getName()),
			//			rawMaterialLocation, 1, sulfuricAcidLocation, 1000, sulfateInfo, 1000, 0);
			//}
			//else {
			helper.registerMineralWasherRecipe(
					new ResourceLocation("jaopca", "electrodynamics.ore_to_sulfate."+material.getName()),
					oreLocation, 1, sulfuricAcidLocation, 1000, sulfateInfo, 1000, 0, 200, 400);
			//}

			helper.registerChemicalCrystallizerRecipe(
					new ResourceLocation("jaopca", "electrodynamics.sulfate_to_crystal."+material.getName()),
					sulfateLocation, 200, crystalInfo, 1, 0, 200, 800);

			helper.registerMineralCrusherRecipe(
					new ResourceLocation("jaopca", "electrodynamics.ore_to_impure_dust."+material.getName()),
					oreLocation, 1, impureDustInfo, 3, 0.3, 200, 450);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerMineralCrusherRecipe(
						new ResourceLocation("jaopca", "electrodynamics.raw_material_to_impure_dust."+material.getName()),
						rawMaterialLocation, 1, impureDustInfo, 3, 0.3, 200, 450);
			}
			helper.registerMineralCrusherRecipe(
					new ResourceLocation("jaopca", "electrodynamics.crystal_to_impure_dust."+material.getName()),
					crystalLocation, 1, impureDustInfo, 1, sulfurTrioxide, 1, 0, 0.1, 200, 450);

			helper.registerMineralGrinderRecipe(
					new ResourceLocation("jaopca", "electrodynamics.impure_dust_to_dust."+material.getName()),
					impureDustLocation, 1, dustLocation, 1, 0.1, 200, 350);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("electrodynamics.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Create's crushing.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			helper.registerMineralGrinderRecipe(
					new ResourceLocation("jaopca", "electrodynamics.ore_to_dust."+material.getName()),
					oreLocation, 1, dustLocation, 2, byproduct, 1, 0.1, 0.3, 200, 350);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerMineralGrinderRecipe(
						new ResourceLocation("jaopca", "electrodynamics.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, 1, dustLocation, 2, 0.1, 200, 350);
			}
		}
	}
}
