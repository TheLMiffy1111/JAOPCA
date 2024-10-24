package thelm.jaopca.compat.create;

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
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "create")
public class CreateModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "zinc"));

	static {
		//if(ModList.get().isLoaded("allthemodium")) {
		//	Collections.addAll(BLACKLIST, "allthemodium", "unobtainium", "vibranium");
		//}
		if(ModList.get().isLoaded("galosphere")) {
			Collections.addAll(BLACKLIST, "silver");
		}
		if(ModList.get().isLoaded("ic2")) {
			Collections.addAll(BLACKLIST, "aluminium", "aluminum", "silver", "tin", "uranium");
		}
		if(ModList.get().isLoaded("iceandfire")) {
			Collections.addAll(BLACKLIST, "silver");
		}
		if(ModList.get().isLoaded("immersiveengineering")) {
			Collections.addAll(BLACKLIST, "aluminium", "aluminum", "lead", "nickel", "silver", "uranium");
		}
		if(ModList.get().isLoaded("mekanism")) {
			Collections.addAll(BLACKLIST, "lead", "osmium", "tin", "uranium");
		}
		if(ModList.get().isLoaded("oreganized")) {
			Collections.addAll(BLACKLIST, "lead");
		}
		if(ModList.get().isLoaded("thermal")) {
			Collections.addAll(BLACKLIST, "lead", "nickel", "silver", "tin");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm crushedForm = ApiImpl.INSTANCE.newForm(this, "create_crushed", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("create:crushed_raw_materials").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "nuggets");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(crushedForm.toRequest());
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
		CreateHelper helper = CreateHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		ResourceLocation deepslateOreLocation = new ResourceLocation("forge:ores_in_ground/deepslate");
		ResourceLocation netherrackOreLocation = new ResourceLocation("forge:ores_in_ground/netherrack");
		ResourceLocation endstoneOreLocation = new ResourceLocation("forge:ores_in_ground/end_stone");
		Item xpNugget = ForgeRegistries.ITEMS.getValue(new ResourceLocation("create:experience_nugget"));
		Item cobbledDeepslate = Items.COBBLED_DEEPSLATE;
		Item netherrack = Items.NETHERRACK;
		Item endstone = Items.END_STONE;
		for(IMaterial material : crushedForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			IItemInfo crushedInfo = itemFormType.getMaterialFormInfo(crushedForm, material);
			ResourceLocation crushedLocation = miscHelper.getTagLocation("create:crushed_raw_materials", material.getName());
			ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("create.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The default byproduct material to output in Create's crushing.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "create.default_ore_to_crushed."+material.getName()),
					CompoundIngredientObject.difference(new Object[] {
							oreLocation,
							deepslateOreLocation, netherrackOreLocation, endstoneOreLocation,
					}), 250, new Object[] {
							crushedInfo, 1,
							crushedInfo, 1, 0.75F,
							xpNugget, 1, 0.75F,
							byproduct, 1, 0.125F,
					});
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "create.deepslate_ore_to_crushed."+material.getName()),
					CompoundIngredientObject.intersection(new Object[] {
							oreLocation, deepslateOreLocation
					}), 350, new Object[] {
							crushedInfo, 2,
							crushedInfo, 1, 0.25F,
							xpNugget, 1, 0.75F,
							cobbledDeepslate, 1, 0.125F,
					});
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "create.netherrack_ore_to_crushed."+material.getName()),
					CompoundIngredientObject.intersection(new Object[] {
							oreLocation, netherrackOreLocation
					}), 350, new Object[] {
							crushedInfo, 1,
							crushedInfo, 1, 0.75F,
							xpNugget, 1, 0.75F,
							netherrack, 1, 0.125F,
					});
			if(itemTags.contains(endstoneOreLocation)) {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "create.end_stone_ore_to_crushed."+material.getName()),
						CompoundIngredientObject.intersection(new Object[] {
								oreLocation, endstoneOreLocation
						}), 350, new Object[] {
								crushedInfo, 1,
								crushedInfo, 1, 0.75F,
								xpNugget, 1, 0.75F,
								endstone, 1, 0.125F,
						});
			}
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "create.raw_material_to_crushed."+material.getName()),
						rawMaterialLocation, 400, new Object[] {
								crushedInfo, 1,
								xpNugget, 1, 0.75F,
						});
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "create.raw_storage_block_to_crushed."+material.getName()),
							rawStorageBlockLocation, 400, new Object[] {
									crushedInfo, 9,
									xpNugget, 9, 0.75F,
							});
				}
			}

			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "create.crushed_to_material_smelting."+material.getName()),
					crushedLocation, materialLocation, 1, 0.1F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "create.crushed_to_material_blasting."+material.getName()),
					crushedLocation, materialLocation, 1, 0.1F, 100);
			helper.registerSplashingRecipe(
					new ResourceLocation("jaopca", "create.crushed_to_nugget."+material.getName()),
					crushedLocation, new Object[] {
							nuggetLocation, 9,
					});
		}

		String[] toRegister = {
				"aluminum", "copper", "gold", "iron", "lead", "nickel", "osmium", "platinum", "quicksilver",
				"silver", "tin", "uranium", "zinc"};
		for(String material : toRegister) {
			api.registerItemTag(
					miscHelper.getTagLocation("create:crushed_raw_materials", material),
					new ResourceLocation("create", "crushed_raw_"+material));
		}
	}
}
