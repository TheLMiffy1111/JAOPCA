package thelm.jaopca.compat.theurgy;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import com.klikli_dev.theurgy.content.item.sulfur.AlchemicalSulfurTier;
import com.klikli_dev.theurgy.content.item.sulfur.AlchemicalSulfurType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.functions.MaterialMappedFunction;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.theurgy.items.JAOPCAAlchemicalSulfurItem;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "theurgy@[1.18,)")
public class TheurgyModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"allthemodium", "amethyst", "apatite", "azure_silver", "cinnabar", "coal", "copper", "crimson_iron",
			"diamond", "emerald", "fluorite", "gold", "iridium", "iron", "lapis", "lead", "netherite", "netherite_scrap",
			"nickel", "osmium", "peridot", "platinum", "prismarine", "quartz", "redstone", "ruby", "sal_ammoniac",
			"sapphire", "silver", "sulfur", "tin", "unobtainium", "uranium", "vibranium", "zinc"));
	public static final Function<IMaterial, AlchemicalSulfurTier> TIER_FUNCTION =
			MaterialMappedFunction.of(AlchemicalSulfurTier.class, AlchemicalSulfurTier.COMMON, "theurgy.tier", "The alchemical sulfur tier of this material.");

	private static boolean addTierTags = false;

	private final IForm alchemicalSulfurForm = ApiImpl.INSTANCE.newForm(this, "theurgy_alchemical_sulfurs", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("theurgy:alchemical_sulfurs").setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(ItemFormType.INSTANCE.getNewSettings().setItemCreator(JAOPCAAlchemicalSulfurItem::new));

	@Override
	public String getName() {
		return "theurgy";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(alchemicalSulfurForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.copyOf(Arrays.asList(MaterialType.ORE));
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		addTierTags = config.getDefinedBoolean("tags.addTierTags", addTierTags, "Should the module add tier tags for alchemical sulfurs.");
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TheurgyHelper helper = TheurgyHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Fluid salAmmoniac = BuiltInRegistries.FLUID.get(ResourceLocation.parse("theurgy:sal_ammoniac"));
		Item mercuryShard = BuiltInRegistries.ITEM.get(ResourceLocation.parse("theurgy:mercury_shard"));
		Item alchemicalSaltMineral = BuiltInRegistries.ITEM.get(ResourceLocation.parse("theurgy:alchemical_salt_mineral"));
		for(IMaterial material : alchemicalSulfurForm.getMaterials()) {
			IItemInfo alchemicalSulfurInfo = itemFormType.getMaterialFormInfo(alchemicalSulfurForm, material);
			ResourceLocation alchemicalSulfurLocation = miscHelper.getTagLocation("theurgy:alchemical_sulfurs", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			int oreCount = switch(material.getType()) {
			case INGOT, INGOT_LEGACY -> 3;
			case GEM, CRYSTAL -> 4;
			default -> 5;
			};

			helper.registerLiquefactionRecipe(
					miscHelper.getRecipeKey("theurgy.ore_to_alchemical_sulfur", material.getName()),
					oreLocation, salAmmoniac, 10,
					alchemicalSulfurInfo, oreCount, 100);
			helper.registerLiquefactionRecipe(
					miscHelper.getRecipeKey("theurgy.material_to_alchemical_sulfur", material.getName()),
					materialLocation, salAmmoniac, 10,
					alchemicalSulfurInfo, 1, 100);

			if(material.getType().isIngot()) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerLiquefactionRecipe(
						miscHelper.getRecipeKey("theurgy.raw_material_to_alchemical_sulfur", material.getName()),
						rawMaterialLocation, salAmmoniac, 10,
						alchemicalSulfurInfo, 2, 100);
			}

			helper.registerIncubationRecipe(
					miscHelper.getRecipeKey("theurgy.alchemical_sulfur_to_material", material.getName()),
					mercuryShard, alchemicalSaltMineral, alchemicalSulfurLocation,
					materialLocation, 1, 100);

			if(addTierTags) {
				String tier = TIER_FUNCTION.apply(material).name().toLowerCase(Locale.US);
				String type = (switch(material.getType()) {
				case INGOT, INGOT_LEGACY -> AlchemicalSulfurType.METALS;
				case GEM, CRYSTAL -> AlchemicalSulfurType.GEMS;
				default -> AlchemicalSulfurType.OTHER_MINERALS;
				}).name().toLowerCase(Locale.US);
				String base = "theurgy:alchemical_sulfurs";
				api.registerItemTag(ResourceLocation.parse(String.join("/", base, tier)), alchemicalSulfurInfo.asItem());
				api.registerItemTag(ResourceLocation.parse(String.join("/", base, type)), alchemicalSulfurInfo.asItem());
				api.registerItemTag(ResourceLocation.parse(String.join("/", base, type, tier)), alchemicalSulfurInfo.asItem());
			}
		}
	}
}
