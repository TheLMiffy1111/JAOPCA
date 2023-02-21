package thelm.jaopca.compat.create;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "create")
public class CreateNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "emerald", "lapis", "quartz", "redstone"));

	static {
		if(ModList.get().isLoaded("appliedenergistics2")) {
			Collections.addAll(BLACKLIST, "certus", "charged_certus");
		}
		if(ModList.get().isLoaded("druidcraft")) {
			Collections.addAll(BLACKLIST, "amber", "fiery_glass", "moonstone");
		}
		if(ModList.get().isLoaded("silentgems")) {
			Collections.addAll(BLACKLIST,
					"agate", "alexandrite", "amazonite", "amber", "amethyst", "ametrine", "ammolite", "apatite", "aquamarine",
					"benitoite", "black_diamond", "carnelian", "cats_eye", "chrysoprase", "citrine", "coral", "euclase",
					"fluorite", "garnet", "green_sapphire", "heliodor", "iolite", "jade", "jasper", "kunzite", "kyanite",
					"lepidoite", "malachite", "moldavite", "moonstone", "morganite", "onyx", "opal", "pearl", "peridot",
					"phosphophyllite", "pyrope", "rose_quartz", "ruby", "sapphire", "sodalite", "spinel", "sunstone",
					"tanzanite", "teklite", "topaz", "turquoise", "yellow_diamond", "zircon");
		}
		if(ModList.get().isLoaded("thermal")) {
			Collections.addAll(BLACKLIST, "apatite", "cinnabar", "niter", "sulfur");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "create_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
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
		Set<ResourceLocation> itemTags = api.getItemTags();
		ResourceLocation deepslateOreLocation = new ResourceLocation("forge:ores_in_ground/deepslate");
		ResourceLocation netherrackOreLocation = new ResourceLocation("forge:ores_in_ground/netherrack");
		ResourceLocation endstoneOreLocation = new ResourceLocation("forge:ores_in_ground/end_stone");
		Item xpNugget = ForgeRegistries.ITEMS.getValue(new ResourceLocation("create:experience_nugget"));
		Item cobbledDeepslate = Items.COBBLED_DEEPSLATE;
		Item netherrack = Items.NETHERRACK;
		Item endstone = Items.END_STONE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("create.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The default byproduct material to output in Create's crushing.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			boolean isCrystal = material.getType() != MaterialType.DUST;

			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "create.default_ore_to_material."+material.getName()),
					CompoundIngredientObject.difference(new Object[] {
							oreLocation,
							deepslateOreLocation, netherrackOreLocation, endstoneOreLocation,
					}), isCrystal ? 350 : 250, new Object[] {
							materialLocation, (isCrystal ? 1 : 3),
							materialLocation, 1, (isCrystal ? 0.75F : 0.5F),
							xpNugget, 1, 0.75F,
							byproduct, 1, 0.125F,
					});
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "create.deepslate_ore_to_material."+material.getName()),
					CompoundIngredientObject.intersection(new Object[] {
							oreLocation, deepslateOreLocation,
					}), isCrystal ? 450 : 350, new Object[] {
							materialLocation, (isCrystal ? 2 : 4),
							materialLocation, 1, (isCrystal ? 0.25F : 0.5F),
							xpNugget, 1, 0.75F,
							cobbledDeepslate, 1, 0.125F,
					});
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "create.netherrack_ore_to_material."+material.getName()),
					CompoundIngredientObject.intersection(new Object[] {
							oreLocation, netherrackOreLocation,
					}), isCrystal ? 450 : 350, new Object[] {
							materialLocation, (isCrystal ? 1 : 3),
							materialLocation, 1, (isCrystal ? 0.75F : 0.5F),
							xpNugget, 1, 0.75F,
							netherrack, 1, 0.125F,
					});
			if(itemTags.contains(endstoneOreLocation)) {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "create.end_stone_ore_to_material."+material.getName()),
						CompoundIngredientObject.intersection(new Object[] {
								oreLocation, endstoneOreLocation,
						}), isCrystal ? 450 : 350, new Object[] {
								materialLocation, (isCrystal ? 1 : 3),
								materialLocation, 1, (isCrystal ? 0.75F : 0.5F),
								xpNugget, 1, 0.75F,
								endstone, 1, 0.125F,
						});
			}
		}
	}
}
