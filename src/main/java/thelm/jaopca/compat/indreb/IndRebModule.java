package thelm.jaopca.compat.indreb;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "indreb@[1.19.2-0.14.3,)")
public class IndRebModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "lead", "netherite", "netherite_scrap", "silver", "tin", "uranium"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "indreb_chunks", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("indreb:chunks").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm purifiedForm = ApiImpl.INSTANCE.newForm(this, "indreb_purified", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("indreb:purified").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, chunkForm, purifiedForm).setGrouped(true);

	@Override
	public String getName() {
		return "indreb";
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
		IndRebHelper helper = IndRebHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Item stoneDust = ForgeRegistries.ITEMS.getValue(new ResourceLocation("indreb:stone_dust"));
		Item mudPile = ForgeRegistries.ITEMS.getValue(new ResourceLocation("indreb:mud_pile"));
		Fluid sulfuricAcid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("indreb:sulfuric_acid"));
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			ResourceLocation chunkLocation = miscHelper.getTagLocation("indreb:chunks", material.getName());
			IItemInfo purifiedInfo = itemFormType.getMaterialFormInfo(purifiedForm, material);
			ResourceLocation purifiedLocation = miscHelper.getTagLocation("indreb:purified", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			int temperature = configs.get(material).getDefinedInt("indreb.temperature", 1200, "The temperature required in the themal centrifuge.");
			helper.registerThermalCentrifugingRecipe(
					new ResourceLocation("jaopca", "indreb.ore_to_chunk."+material.getName()),
					oreLocation, 1,
					chunkInfo, 4, stoneDust, 1, 100F,
					temperature, 500, 46, 0.1F);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerThermalCentrifugingRecipe(
						new ResourceLocation("jaopca", "indreb.raw_material_to_chunk."+material.getName()),
						rawMaterialLocation, 3,
						chunkInfo, 8,
						temperature, 500, 46, 0.1F);
			}

			helper.registerOreWashingRecipe(
					new ResourceLocation("jaopca", "indreb.ore_to_purified."+material.getName()),
					oreLocation, 1, Fluids.WATER, 750,
					purifiedInfo, 3, stoneDust, 1, 100F,
					500, 16, 0.1F);
			helper.registerOreWashingRecipe(
					new ResourceLocation("jaopca", "indreb.chunk_to_purified."+material.getName()),
					chunkLocation, 1, sulfuricAcid, 250,
					purifiedInfo, 1, mudPile, 1, 100F,
					500, 16, 0.1F);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerOreWashingRecipe(
						new ResourceLocation("jaopca", "indreb.raw_material_to_purified."+material.getName()),
						rawMaterialLocation, 1, Fluids.WATER, 500,
						purifiedInfo, 2,
						500, 16, 0.1F);
			}

			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "indreb.purified_to_dust."+material.getName()),
					purifiedLocation, 1,
					dustLocation, 1,
					180, 8, 0.2F);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "indreb.ore_to_dust."+material.getName()),
					oreLocation, 1,
					dustLocation, 2,
					180, 8, 0.2F);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "indreb.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, 3,
						dustLocation, 4,
						180, 8, 0.2F);
			}
		}
	}
}
