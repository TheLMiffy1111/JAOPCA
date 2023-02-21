package thelm.jaopca.compat.bcadditions;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "bcadditions")
public class BCAdditionsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet(Arrays.asList(
			"Adamantine", "Alduorite", "Aluminium", "AluminiumBrass", "Aluminum", "AluminumBrass", "Alumite",
			"Amordrine", "Angmallen", "Ardite", "AstralSilver", "Atlarus", "BlackSteel", "Blutonium", "Brass",
			"Bronze", "Carmot", "Celenegil", "Ceruclase", "Cobalt", "ConductiveIron", "Copper", "Cyanite",
			"DamascusSteel", "DarkSteel", "DeepIron", "Desichalkos", "ElectricalSteel", "Electrum",
			"EnergeticAlloy", "Eximite", "FzDarkIron", "GildedRedMetal", "Gold", "Graphite", "Haderoth",
			"Hepatizon", "Ignatius", "Infuscolium", "Inolashite", "Invar", "Iron", "Kalendrite", "Lead",
			"Lemurite", "Ludicrite", "Lumium", "Manganese", "Manyullyn", "Meutoite", "Midasium", "Mithril",
			"NaturalAluminum", "Nickel", "Orichalcum", "Osmium", "Oureclase", "PhasedGold", "PhasedIron",
			"PigIron", "Platinum", "Prometheum", "Quicksilver", "RedstoneAlloy", "Rubracium", "Sanguinite",
			"ShadowIron", "ShadowSteel", "Signalum", "Silver", "Soularium", "Steel", "Tartarite", "Tin",
			"Vulcanite", "Vyroxeres", "Yellorite", "Yellorium", "Zinc"));

	@Override
	public String getName() {
		return "bcadditions";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		BCAdditionsHelper helper = BCAdditionsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			helper.registerDusterRecipe(
					miscHelper.getRecipeKey("bcadditions.ore_to_dust", material.getName()),
					oreOredict, dustOredict, 2);
		}
	}
}
