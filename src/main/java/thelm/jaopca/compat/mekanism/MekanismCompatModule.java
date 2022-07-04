package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "mekanism")
public class MekanismCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Amber", "Charcoal", "Coal", "Copper", "Diamond", "Draconium", "Emerald",
			"Gold", "Iridium", "Iron", "Lapis", "Lead", "Malachite", "Mithril", "Nickel", "Osmium", "Peridot",
			"Platinum", "Quartz", "Redstone", "RefinedGlowstone", "RefinedObsidian", "Ruby", "Sapphire", "Silver",
			"Steel", "Tanzanite", "Tin", "Topaz", "Uranium"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Amber", "Charcoal", "Coal", "Diamond", "Emerald", "Lapis", "Malachite", "Peridot", "Quartz",
			"Ruby", "Sapphire", "Tanzanite", "Topaz"));
	private static final Set<String> TO_ORE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Amber", "Amethyst", "Apatite", "Coal", "Copper", "Diamond", "Draconium",
			"Emerald", "Gold", "Iridium", "Iron", "Lapis", "Lead", "Malachite", "Mithril", "Nickel", "Osmium",
			"Peridot", "Platinum", "Quartz", "Redstone", "RefinedGlowstone", "RefinedObsidian", "Ruby", "Sapphire",
			"Silver", "Steel", "Tanzanite", "Tin", "Topaz", "Uranium"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configToOreBlacklist = new TreeSet<>();

	static {
		if(Loader.isModLoaded("appliedenergistics2")) {
			Collections.addAll(TO_DUST_BLACKLIST, "CertusQuartz", "ChargedCertusQuartz", "Fluix");
			Collections.addAll(TO_CRYSTAL_BLACKLIST, "CertusQuartz", "Fluix");
		}
		if(Loader.isModLoaded("metallurgy")) {
			String[] list = {
					"Adamantine", "Alduorite", "Amordrine", "Angmallen", "AstralSilver", "Atlarus", "BlackSteel",
					"Brass", "Bronze", "Carmot", "Celenegil", "Ceruclase", "DamascusSteel", "DeepIron",
					"Desichalkos", "Electrum", "Eximite", "Haderoth", "Hepatizon", "Ignatius", "Infuscolium",
					"Inolashite", "Kalendrite", "Lemurite", "Manganese", "Meutoite", "Midasium", "Orichalcum",
					"Oureclase", "Prometheum", "Quicksilver", "Rubracium", "Sanguinite", "ShadowIron",
					"ShadowSteel", "Tartarite", "Vulcanite", "Vyroxeres", "Zinc",
			};
			Collections.addAll(TO_DUST_BLACKLIST, list);
			Collections.addAll(TO_ORE_BLACKLIST, list);
		}
		if(Loader.isModLoaded("mysticalagriculture")) {
			Collections.addAll(TO_ORE_BLACKLIST, "Inferium", "Prosperity");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "mekanism_compat";
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
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have enriching to material recipes added."),
				configToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toOreMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have combining to ore recipes added."),
				configToOreBlacklist);
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerCrusherRecipe(
							miscHelper.getRecipeKey("mekanism.material_to_dust", name),
							materialOredict, 1, dustOredict, 1);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(oredict.contains(dustOredict)) {
					helper.registerEnrichmentChamberRecipe(
							miscHelper.getRecipeKey("mekanism.dust_to_material", name),
							dustOredict, 1, materialOredict, 1);
				}
			}
			if(type.isOre() && !TO_ORE_BLACKLIST.contains(name) && !configToOreBlacklist.contains(name)) {
				String ingOredict = miscHelper.getOredictName("dust", name);
				String oreOredict = miscHelper.getOredictName("ore", name);
				if(!oredict.contains(ingOredict)) {
					ingOredict = miscHelper.getOredictName(type.getFormName(), name);
				}
				if(oredict.contains(ingOredict)) {
					IDynamicSpecConfig config = configs.get(material);
					String configOreBase = config.getDefinedString("mekanism.oreBase", "minecraft:cobblestone",
							this::isOredictOrItemValid, "The default base to use in Mekanism's Combiner to recreate ores.");
					Object oreBase = getOredictOrItem(configOreBase);
					helper.registerCombinerRecipe(
							miscHelper.getRecipeKey("mekanism.material_to_ore", name),
							ingOredict, type.isCrystalline() ? 5 : 8, oreBase, 1, oreOredict, 1);
				}
			}
		}
	}

	public boolean isOredictOrItemValid(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return ApiImpl.INSTANCE.getOredict().contains(s.substring(1));
		}
		else {
			return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s.split("@(?=\\d*$)")[0]));
		}
	}

	public Object getOredictOrItem(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return s.substring(1);
		}
		else {
			return MiscHelper.INSTANCE.parseMetaItem(s);
		}
	}
}
