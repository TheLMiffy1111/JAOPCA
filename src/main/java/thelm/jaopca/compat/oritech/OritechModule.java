package thelm.jaopca.compat.oritech;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
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

@JAOPCAModule(modDependencies = "oritech@[0.15.3,)")
public class OritechModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "netherite_scrap", "nickel", "platinum", "uranium"));
	private static final Set<String> RAW_BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "netherite_scrap", "nickel", "platinum", "uranium"));

	private static boolean commonClump = true;

	static {
		if(ModList.get().isLoaded("create")) {
			Collections.addAll(RAW_BLACKLIST, "zinc");
		}
		if(ModList.get().isLoaded("energizedpower")) {
			Collections.addAll(RAW_BLACKLIST, "tin");
		}
		if(ModList.get().isLoaded("immersiveengineering")) {
			Collections.addAll(RAW_BLACKLIST, "aluminium", "aluminum", "lead", "silver");
		}
		if(ModList.get().isLoaded("mekanism")) {
			Collections.addAll(RAW_BLACKLIST, "lead", "osmium", "tin");
		}
	}

	private final IForm clumpForm = ApiImpl.INSTANCE.newForm(this, "oritech_clumps", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("oritech:clumps").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm smallClumpForm = ApiImpl.INSTANCE.newForm(this, "oritech_small_clumps", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("oritech:small_clumps").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm gemForm = ApiImpl.INSTANCE.newForm(this, "oritech_gems", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("oritech:gems").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			clumpForm, smallClumpForm, gemForm).setGrouped(true);

	@Override
	public String getName() {
		return "oritech";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "tiny_dusts");
		builder.put(1, "tiny_dusts");
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
		return RAW_BLACKLIST;
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		commonClump = config.getDefinedBoolean("tags.commonClump", commonClump, "Should the module mainly use c:clumps instead of oritech:clumps.");
	}

	@Override
	public void onMaterialComputeComplete(IModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper helper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : formRequest.getMaterials()) {
			if(commonClump) {
				IItemInfo clumpInfo = itemFormType.getMaterialFormInfo(clumpForm, material);
				api.registerItemTag(helper.getTagLocation("clumps", material.getName()), clumpInfo.asItem());
			}
		}
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		OritechHelper helper = OritechHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Fluid sheolFire = BuiltInRegistries.FLUID.get(ResourceLocation.parse("oritech:still_sheol_fire"));
		Fluid sulfuricAcid = BuiltInRegistries.FLUID.get(ResourceLocation.parse("oritech:still_sulfuric_acid"));
		Fluid mineralSlurry = BuiltInRegistries.FLUID.get(ResourceLocation.parse("oritech:still_mineral_slurry"));
		Item fluxite = BuiltInRegistries.ITEM.get(ResourceLocation.parse("oritech:fluxite"));
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo clumpInfo = itemFormType.getMaterialFormInfo(clumpForm, material);
			ResourceLocation clumpLocation = miscHelper.getTagLocation(commonClump ? "clumps" : "oritech:clumps", material.getName());
			IItemInfo smallClumpInfo = itemFormType.getMaterialFormInfo(smallClumpForm, material);
			ResourceLocation smallClumpLocation = miscHelper.getTagLocation("oritech:small_clumps", material.getName());
			IItemInfo gemInfo = itemFormType.getMaterialFormInfo(gemForm, material);
			ResourceLocation gemLocation = miscHelper.getTagLocation("oritech:gems", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation extraTinyDustLocation = miscHelper.getTagLocation("tiny_dusts", material.getExtra(1).getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			ResourceLocation createCrushedLocation = miscHelper.getTagLocation("create:crushed_raw_materials", material.getName());
			CompoundIngredientObject clumpIngredient = CompoundIngredientObject.union(clumpLocation, createCrushedLocation);

			if(material.hasExtra(1)) {
				IMaterial extraMaterial = material.getExtra(1);
				String extraForm = switch(extraMaterial.getType()) {
				case INGOT, INGOT_LEGACY -> "oritech:small_clumps";
				case GEM, GEM_PLAIN -> "gems";
				case CRYSTAL, CRYSTAL_PLAIN -> "crystals";
				default -> "dusts";
				};
				ResourceLocation extraLocation = miscHelper.getTagLocation(extraForm, extraMaterial.getName());
				// in case uranium is used as extra
				if(!itemTags.contains(extraLocation)) {
					extraLocation = miscHelper.getTagLocation("dusts", extraMaterial.getName());
				}
				int extraCount = switch(extraMaterial.getType()) {
				case INGOT, INGOT_LEGACY -> 3;
				default -> 1;
				};
				if(material.getType() == MaterialType.INGOT) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("oritech.raw_material_to_clump", material.getName()),
							rawMaterialLocation, clumpInfo, 1, smallClumpInfo, 3, extraLocation, extraCount, 140);
				}
				else {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("oritech.ore_to_clump", material.getName()),
							oreLocation, clumpInfo, 1, smallClumpInfo, 3, extraLocation, extraCount, 140);
				}
			}
			else {
				if(material.getType() == MaterialType.INGOT) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("oritech.raw_material_to_clump", material.getName()),
							rawMaterialLocation, clumpInfo, 1, smallClumpInfo, 3, 140);
				}
				else {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("oritech.ore_to_clump", material.getName()),
							oreLocation, clumpInfo, 1, smallClumpInfo, 3, 140);
				}
			}

			if(material.getType() == MaterialType.INGOT) {
				helper.registerRefineryRecipe(
						miscHelper.getRecipeKey("oritech.raw_material_to_clump_refinery", material.getName()),
						rawMaterialLocation, sheolFire, 250, clumpInfo, 3, Fluids.LAVA, 200, 200);
			}
			else {
				helper.registerRefineryRecipe(
						miscHelper.getRecipeKey("oritech.ore_to_clump_refinery", material.getName()),
						oreLocation, sheolFire, 250, clumpInfo, 3, Fluids.LAVA, 200, 200);
			}

			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("oritech.small_clump_to_clump", material.getName()),
					clumpInfo, 1, new Object[] {
							smallClumpLocation, smallClumpLocation, smallClumpLocation,
							smallClumpLocation, smallClumpLocation, smallClumpLocation,
							smallClumpLocation, smallClumpLocation, smallClumpLocation,
					});

			helper.registerCentrifugeRecipe(
					miscHelper.getRecipeKey("oritech.clump_to_gem_dry", material.getName()),
					clumpIngredient, gemInfo, 1, extraTinyDustLocation, 3, 300);
			helper.registerCentrifugeFluidRecipe(
					miscHelper.getRecipeKey("oritech.clump_to_gem_wet", material.getName()),
					clumpIngredient, Fluids.WATER, 1000, gemInfo, 2, Fluids.EMPTY, 0, 300);
			helper.registerCentrifugeFluidRecipe(
					miscHelper.getRecipeKey("oritech.clump_to_gem_acid", material.getName()),
					clumpIngredient, sulfuricAcid, 1000, gemInfo, 3, mineralSlurry, 250, 300);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("oritech.gem_to_material_smelting", material.getName()),
					gemLocation, materialLocation, 1, 1F, 200);
			api.registerBlastingRecipe(
					miscHelper.getRecipeKey("oritech.gem_to_material_blasting", material.getName()),
					gemLocation, materialLocation, 1, 1F, 100);
			helper.registerFoundryRecipe(
					miscHelper.getRecipeKey("oritech.gem_to_material_foundry", material.getName()),
					gemLocation, gemLocation, materialLocation, 3, 200);

			helper.registerAtomicForgeRecipe(
					miscHelper.getRecipeKey("oritech.gem_to_dust", material.getName()),
					gemLocation, fluxite, fluxite, dustLocation, 2, 20);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ResourceLocation tinyDustLocation = miscHelper.getTagLocation("tiny_dusts", material.getName());

			if(material.getType() == MaterialType.INGOT) {
				helper.registerPulverizerRecipe(
						miscHelper.getRecipeKey("oritech.ore_to_raw_material_pulverizer", material.getName()),
						oreLocation, rawMaterialLocation, 2, 200);
				helper.registerPulverizerRecipe(
						miscHelper.getRecipeKey("oritech.raw_material_to_dust", material.getName()),
						rawMaterialLocation, dustLocation, 1, tinyDustLocation, 3, 200);

				if(material.hasExtra(1)) {
					IMaterial extraMaterial = material.getExtra(1);
					String extraForm = switch(extraMaterial.getType()) {
					case INGOT -> "raw_materials";
					case GEM, GEM_PLAIN -> "gems";
					case CRYSTAL, CRYSTAL_PLAIN -> "crystals";
					default -> "dusts";
					};
					ResourceLocation extraLocation = miscHelper.getTagLocation(extraForm, extraMaterial.getName());
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("oritech.ore_to_raw_material_grinder", material.getName()),
							oreLocation, dustLocation, 2, extraLocation, 1, 140);
				}
				else {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("oritech.ore_to_raw_material_grinder", material.getName()),
							oreLocation, dustLocation, 2, 140);
				}
			}
			else {
				helper.registerPulverizerRecipe(
						miscHelper.getRecipeKey("oritech.ore_to_dust", material.getName()),
						oreLocation, dustLocation, 2, 200);
			}
		}
	}
}
