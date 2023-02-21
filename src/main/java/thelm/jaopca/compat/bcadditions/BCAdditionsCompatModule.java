package thelm.jaopca.compat.bcadditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "bcadditions")
public class BCAdditionsCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Adamantine", "Alduorite", "Aluminium", "AluminiumBrass", "Aluminum", "AluminumBrass", "Alumite",
			"Amordrine", "Angmallen", "Apatite", "Ardite", "AstralSilver", "Atlarus", "BlackSteel", "Blutonium",
			"Brass", "Bronze", "Carmot", "Celenegil", "Ceruclase", "Charcoal", "Coal", "Cobalt", "ConductiveIron",
			"Copper", "Cyanite", "DamascusSteel", "DarkSteel", "DeepIron", "Desichalkos", "Diamond",
			"ElectricalSteel", "Electrum", "Emerald", "Enderium", "EnderiumBase", "EnderPearl", "EnergeticAlloy",
			"Eximite", "FzDarkIron", "GildedRedMetal", "Gold", "Graphite", "Haderoth", "Hepatizon", "Ignatius",
			"Infuscolium", "Inolashite", "Invar", "Iron", "Kalendrite", "Lead", "Lemurite", "Ludicrite", "Lumium",
			"Manganese", "Manyullyn", "Meutoite", "Midasium", "Mithril", "NaturalAluminum", "NetherQuartz",
			"Nickel", "Obsidian", "Orichalcum", "Osmium", "Oureclase", "PhasedGold", "PhasedIron", "PigIron",
			"Platinum", "Prometheum", "Quicksilver", "RedstoneAlloy", "Rubracium", "Sanguinite", "ShadowIron",
			"ShadowSteel", "Signalum", "Silver", "Soularium", "Steel", "Tartarite", "Tin", "Vulcanite",
			"Vyroxeres","Yellorite", "Yellorium", "Zinc"));
	private static Set<String> configMaterialToDustBlacklist = new TreeSet<>();
	private static Set<String> configBlockToDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "bcadditions_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have duster material to dust recipes added."),
				configMaterialToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have duster block to dust recipes added."),
				configBlockToDustBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		BCAdditionsHelper helper = BCAdditionsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configMaterialToDustBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerDusterRecipe(
							miscHelper.getRecipeKey("bcadditions.material_to_dust", name),
							materialOredict, dustOredict, 1);
				}
			}
			if(type.isIngot() && !TO_DUST_BLACKLIST.contains(name) && !configBlockToDustBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerDusterRecipe(
							miscHelper.getRecipeKey("bcadditions.block_to_dust", name),
							blockOredict, dustOredict, material.isSmallStorageBlock() ? 4 : 9);
				}
			}
		}
	}
}
