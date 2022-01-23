package thelm.jaopca.compat.crossroads;

import java.util.Arrays;
import java.util.Collections;
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

@JAOPCAModule(modDependencies = "crossroads")
public class CrossroadsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "tin"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm gritForm = ApiImpl.INSTANCE.newForm(this, "crossroads_grits", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("crossroads:grits").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm clumpForm = ApiImpl.INSTANCE.newForm(this, "crossroads_clumps", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("crossroads:clumps").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "crossroads";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "nuggets");
		builder.put(0, "dusts");
		builder.put(0, "molten");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this, gritForm, clumpForm));
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
		JAOPCAApi api = ApiImpl.INSTANCE;
		CrossroadsHelper helper = CrossroadsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : gritForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			IItemInfo gritInfo = itemFormType.getMaterialFormInfo(gritForm, material);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerStampMillRecipe(
						new ResourceLocation("jaopca", "crossroads.raw_material_to_grit."+material.getName()),
						rawMaterialLocation, gritInfo, 3);
			}
			else {
				helper.registerStampMillRecipe(
						new ResourceLocation("jaopca", "crossroads.ore_to_grit."+material.getName()),
						oreLocation, gritInfo, 3);
			}
		}
		for(IMaterial material : clumpForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation gritLocation = miscHelper.getTagLocation("crossroads:grits", material.getName());
			IItemInfo clumpInfo = itemFormType.getMaterialFormInfo(clumpForm, material);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerOreCleanserRecipe(
						new ResourceLocation("jaopca", "crossroads.raw_material_to_clump."+material.getName()),
						rawMaterialLocation, clumpInfo, 2);
			}
			else {
				helper.registerOreCleanserRecipe(
						new ResourceLocation("jaopca", "crossroads.ore_to_clump."+material.getName()),
						oreLocation, clumpInfo, 2);
			}
			helper.registerOreCleanserRecipe(
					new ResourceLocation("jaopca", "crossroads.grit_to_clump."+material.getName()),
					gritLocation, clumpInfo, 1);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation gritLocation = miscHelper.getTagLocation("crossroads:grits", material.getName());
			ResourceLocation clumpLocation = miscHelper.getTagLocation("crossroads:clumps", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
			ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("crossroads.byproduct", "minecraft:sand",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Crossroads' Millstone.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerMillRecipe(
						new ResourceLocation("jaopca", "crossroads.ore_to_raw_material."+material.getName()),
						oreLocation, rawMaterialLocation, 2, byproduct, 1);
				helper.registerMillRecipe(
						new ResourceLocation("jaopca", "crossroads.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, dustLocation, 2);
				helper.registerBlastFurnaceRecipe(
						new ResourceLocation("jaopca", "crossroads.raw_material_to_molten."+material.getName()),
						rawMaterialLocation, moltenLocation, 180, 4);
				helper.registerCrucibleRecipe(
						new ResourceLocation("jaopca", "crossroads.raw_material_to_molten_crucible."+material.getName()),
						rawMaterialLocation, moltenLocation, 180);
			}
			else {
				helper.registerMillRecipe(
						new ResourceLocation("jaopca", "crossroads.ore_to_dust."+material.getName()),
						oreLocation, dustLocation, 2, byproduct, 1);
				helper.registerBlastFurnaceRecipe(
						new ResourceLocation("jaopca", "crossroads.ore_to_molten."+material.getName()),
						oreLocation, moltenLocation, 180, 4);
				helper.registerCrucibleRecipe(
						new ResourceLocation("jaopca", "crossroads.ore_to_molten_crucible."+material.getName()),
						oreLocation, moltenLocation, 180);
			}

			helper.registerBlastFurnaceRecipe(
					new ResourceLocation("jaopca", "crossroads.grit_to_molten."+material.getName()),
					gritLocation, moltenLocation, 90, 2);
			helper.registerBlastFurnaceRecipe(
					new ResourceLocation("jaopca", "crossroads.clump_to_molten."+material.getName()),
					clumpLocation, moltenLocation, 90, 1);

			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "crossroads.grit_to_nugget."+material.getName()),
					gritLocation, nuggetLocation, 7, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "crossroads.grit_to_nugget_blasting."+material.getName()),
					gritLocation, nuggetLocation, 7, 0.7F, 100);
			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "crossroads.clump_to_nugget."+material.getName()),
					clumpLocation, nuggetLocation, 7, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "crossroads.clump_to_nugget_blasting."+material.getName()),
					clumpLocation, nuggetLocation, 7, 0.7F, 100);
		}
	}
}
