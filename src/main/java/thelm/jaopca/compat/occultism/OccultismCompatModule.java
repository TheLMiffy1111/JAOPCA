package thelm.jaopca.compat.occultism;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "occultism")
public class OccultismCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"adamant", "allthemodium", "aluminium", "aluminum", "amber", "amethyst", "annealed_copper", "antimony",
			"apatite", "arcane_crystal", "azure_electrum", "azure_silver", "battery_alloy", "beryllium", "biosteel",
			"black_quartz", "blaze_gold", "brass", "brick", "bronze", "cadmium", "certus_quartz", "charcoal",
			"chromium", "cinnabar", "coal", "coal_coke", "cobalt", "constantan", "copper", "crimson_iron",
			"crimson_steel", "cupronickel", "dark_gem", "diamond", "duratium", "electrum", "emerald", "ender_pearl",
			"enderium", "energite", "entro", "fluix", "fluorite", "gold", "graphite", "he_mox", "he_uranium",
			"hop_graphite", "iesnium", "invar", "iridium", "iron", "kanthal", "lapis", "le_mox", "le_uranium",
			"lead", "lignite_coal", "lumium", "mithril", "netherite", "netherite_scrap", "nickel", "osmium", "peridot",
			"pewter", "platinum", "plutonium", "quartz", "quicksilver", "redstone", "refined_obsidian", "ruby",
			"sapphire", "signalum", "silicon", "silver", "steel", "sulfur", "superconductor", "tin", "titanium",
			"topaz", "tungsten", "tyrian_steel", "unobtainium", "unobtainium_allthemodium_alloy",
			"unobtainium_vibranium_alloy", "uranium", "uranium_235", "uranium_238", "vibranium",
			"vibranium_allthemodium_alloy", "zinc"));
	private static final Set<String> TO_DIRTY_DUST_BLACKLIST = new TreeSet<>(List.of(
			"allthemodium", "aluminium", "aluminum", "antimony", "azure_silver", "cobalt", "copper", "crimson_iron",
			"gold", "graphite", "iesnium", "iridium", "iron", "lead", "mithril", "nickel", "osmium", "pewter", "platinum",
			"quicksilver", "silver", "tin", "titanium", "tungsten", "unobtainium", "uranium", "vibranium", "zinc"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToDirtyDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "occultism_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crushing to dust recipes added."),
				configToDustBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		OccultismHelper helper = OccultismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerCrushingRecipe(
							miscHelper.getRecipeKey("occultism.material_to_dust", name),
							materialLocation, dustLocation, 1, 200, true);
				}
			}
			if(type.isIngot() && !TO_DIRTY_DUST_BLACKLIST.contains(name) && !configToDirtyDustBlacklist.contains(name)) {
				ResourceLocation clumpLocation = miscHelper.getTagLocation("clumps", name);
				ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("dirty_dusts", name);
				if(itemTags.contains(clumpLocation) && itemTags.contains(dirtyDustLocation)) {
					helper.registerCrushingRecipe(
							miscHelper.getRecipeKey("occultism.clump_to_dirty_dust", name),
							clumpLocation, dirtyDustLocation, 2, 200, false);
				}
			}
		}
	}
}
