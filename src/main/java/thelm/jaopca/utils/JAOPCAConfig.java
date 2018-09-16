package thelm.jaopca.utils;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultiset;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.ore.OreEntry;
import thelm.jaopca.ore.OreFinder;

public class JAOPCAConfig {

	public static Configuration configFile;
	public static ArrayList<String> usedCategories = Lists.newArrayList();
	public static TreeMultiset<String> moduleBlacklist = TreeMultiset.create();
	public static boolean ingot;
	public static boolean gem;
	public static boolean dust;
	public static boolean ingot_oreless;
	public static boolean gem_oreless;
	public static boolean reloadColors;
	public static int colorMode;

	public static final ArrayList<String> FRONT_MOD_IDS = Lists.newArrayList("minecraft");
	public static final ArrayList<String> BACK_MOD_IDS = Lists.newArrayList("exnihiloomnia", "exnihiloadscensio", "exnihilocreatio", "jaopca");

	public static final String[] DEFAULT_SHOULD_BE_GEM = {"Coal", "Lapis", "Diamond", "Emerald", "Quartz"};
	public static final String[] DEFAULT_SHOULD_BE_DUST = {"Redstone"};

	public static void init(File file) {
		configFile = new Configuration(file);
		initModConfigs();
	}

	public static void initModConfigs() {
		String name = "jaopca";

		Collections.addAll(Utils.MOD_IDS, configFile.get(name, "orePreference", new String[0], "Mods that are preferred for their items.").setRequiresMcRestart(true).getStringList());
		Utils.MOD_IDS.addAll(FRONT_MOD_IDS);
		for(ModContainer mod : Loader.instance().getActiveModList()) {
			String modId = mod.getModId();
			if(!Utils.MOD_IDS.contains(modId) && !BACK_MOD_IDS.contains(modId)) {
				Utils.MOD_IDS.add(modId);
			}
		}
		Utils.MOD_IDS.addAll(BACK_MOD_IDS);

		Collections.addAll(moduleBlacklist, configFile.get(name, "moduleBlacklist", new String[0], "The global module blacklist. * will mean all modules. Along with material module blacklists, if a module name occurs an odd number of times for a material, then the module is disabled for the material.").setRequiresMcRestart(true).getStringList());
		int count = moduleBlacklist.count("*");
		JAOPCAApi.NAME_TO_MODULE_MAP.keySet().forEach(s->moduleBlacklist.add(s, count));
		moduleBlacklist.remove("*", count);

		Collections.addAll(OreFinder.SHOULD_BE_GEMS, configFile.get(name, "oreShouldBeGem", DEFAULT_SHOULD_BE_GEM, "List of materials that should be gems.").setRequiresMcRestart(true).getStringList());
		Collections.addAll(OreFinder.SHOULD_BE_DUSTS, configFile.get(name, "oreShouldBeDust", DEFAULT_SHOULD_BE_DUST, "List of materials that should be dusts.").setRequiresMcRestart(true).getStringList());
		Collections.addAll(OreFinder.CONFLICT_PRECEDENCE, configFile.get(name, "oreConflictPrecedence", new String[0], "List of material names that should take precedence when a naming conflict occurs.").setRequiresMcRestart(true).getStringList());

		ingot = configFile.get(name, "ingot", true, "Enables the finding of ingot ores.").setRequiresMcRestart(true).getBoolean();
		gem = configFile.get(name, "gem", true, "Enables the finding of gem ores.").setRequiresMcRestart(true).getBoolean();
		dust = configFile.get(name, "dust", true, "Enables the finding of dust ores.").setRequiresMcRestart(true).getBoolean();
		ingot_oreless = configFile.get(name, "ingot_oreless", false, "Enables the finding of ingots with no ores.").setRequiresMcRestart(true).getBoolean();
		gem_oreless = configFile.get(name, "gem_oreless", false, "Enables the finding of gems with no ores.").setRequiresMcRestart(true).getBoolean();

		reloadColors = configFile.get(name, "reloadColorConfigs", false, "Set to true to reload color configs.").setRequiresMcRestart(true).getBoolean();
		colorMode = configFile.get(name, "colorMode", 0, "Mode for average color calculation. 0 is Color RMS, 1 is ColorThief.").getInt();

		if(reloadColors) {
			configFile.getCategory(name).get("reloadColorConfigs").setToDefault();
		}

		usedCategories.add(name);

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	public static void initOreConfigs(List<OreEntry> allOres) {
		for(OreEntry entry : allOres) {
			String name = Utils.to_under_score(entry.getOreName());

			String[] synonyms = entry.getOreNameSynonyms().toArray(new String[entry.getOreNameSynonyms().size()]);
			entry.setOreNameSynonyms(Arrays.asList(configFile.get(name, "synonyms", synonyms, "The synonyms for this ore to use for Ore Dictionary registration.").setRequiresMcRestart(true).getStringList()));

			if(entry.getOreType().ordinal() < 3) {
				String originalExtra = entry.getExtra();
				String configExtra = configFile.get(name, "extra", originalExtra, "The main byproduct material for this material.").setRequiresMcRestart(true).getString();
				boolean doesOreExist = Utils.doesOreNameExist("ore"+configExtra);

				if(doesOreExist) {
					entry.setExtra(configExtra);
				}
				else {
					JAOPCAApi.LOGGER.warn("Found invalid extra name in ore entry "+entry.getOreName()+", replacing");
					configFile.getCategory(name).get("extra").setToDefault();
					entry.setExtra(originalExtra);
				}

				String originalExtra2 = entry.getSecondExtra();
				String configExtra2 = configFile.get(name, "extra2", originalExtra2, "The secondary byproduct material for this material.").setRequiresMcRestart(true).getString();
				boolean doesOreExist2 = Utils.doesOreNameExist("ore"+configExtra2);

				if(doesOreExist2) {
					entry.setSecondExtra(configExtra2);
				}
				else {
					JAOPCAApi.LOGGER.warn("Found invalid second extra name in ore entry "+entry.getOreName()+", replacing");
					configFile.getCategory(name).get("extra2").setToDefault();
					entry.setSecondExtra(originalExtra2);
				}

				String originalExtra3 = entry.getThirdExtra();
				String configExtra3 = configFile.get(name, "extra3", originalExtra3, "The tertiary byproduct material for this material.").setRequiresMcRestart(true).getString();
				boolean doesOreExist3 = Utils.doesOreNameExist("ore"+configExtra3);

				if(doesOreExist2) {
					entry.setThirdExtra(configExtra2);
				}
				else {
					JAOPCAApi.LOGGER.warn("Found invalid third extra name in ore entry "+entry.getOreName()+", replacing");
					configFile.getCategory(name).get("extra3").setToDefault();
					entry.setThirdExtra(originalExtra3);
				}
			}

			entry.setEnergyModifier(configFile.get(name, "energyModifier", entry.getEnergyModifier(), "The energy modifier of this material. Used to calculated energy costs.").setRequiresMcRestart(true).getDouble());
			entry.setRarity(configFile.get(name, "rarity", entry.getRarity(), "The rarity of this material. Used to calculate default chances.").setRequiresMcRestart(true).getDouble());
			entry.setHasEffect(configFile.get(name, "hasEffect", entry.getHasEffect(), "Does this material have a enchanted glow.").setRequiresMcRestart(true).getBoolean());

			TreeMultiset<String> blacklist = TreeMultiset.create(Arrays.asList(configFile.get(name, "moduleBlacklist", new String[0], "The module blacklist for this material. * will mean all modules.").setRequiresMcRestart(true).getStringList()));
			int count = blacklist.count("*");
			JAOPCAApi.NAME_TO_MODULE_MAP.keySet().forEach(s->blacklist.add(s, count));
			blacklist.remove("*", count);

			entry.addBlacklistedModules(Multisets.sum(moduleBlacklist, blacklist).entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(e->e.getElement()).collect(Collectors.toCollection(()->Sets.<String>newTreeSet())));

			usedCategories.add(name);
		}

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	public static void preInitModulewiseConfigs() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			module.registerConfigsPre(configFile);
		}

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	public static void initModulewiseConfigs() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			module.registerConfigs(configFile);
		}

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	public static void initColorConfigs(OreEntry entry) {
		String name = Utils.to_under_score(entry.getOreName());

		if(reloadColors) {
			configFile.getCategory(name).remove("color");
		}

		entry.setColor(Color.decode(configFile.get(name, "color", "0x"+Integer.toHexString(entry.getColor() & 0xFFFFFF), "The color for this material.").getString()));

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}
}
