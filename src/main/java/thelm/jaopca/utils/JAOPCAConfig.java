package thelm.jaopca.utils;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.ore.OreEntry;

public class JAOPCAConfig {

	public static Configuration configFile;
	public static ArrayList<String> usedCategories = Lists.<String>newArrayList();
	public static ArrayList<String> moduleBlacklist = Lists.<String>newArrayList();
	public static boolean ingot;
	public static boolean gem;
	public static boolean dust;
	public static boolean ingot_oreless;
	public static boolean gem_oreless;
	public static boolean reloadColors;

	public static final ArrayList<String> FRONT_MOD_IDS = Lists.<String>newArrayList("minecraft");
	public static final ArrayList<String> BACK_MOD_IDS = Lists.<String>newArrayList("exnihiloadscensio", "jaopca");

	public static void init(File file) {
		configFile = new Configuration(file);
		initModConfigs();
	}

	public static void initModConfigs() {
		String name = "jaopca";

		Utils.MOD_IDS.addAll(Arrays.asList(configFile.get(name, "orePreference", new String[0]).setRequiresMcRestart(true).getStringList()));
		Utils.MOD_IDS.addAll(FRONT_MOD_IDS);
		for(ModContainer mod : Loader.instance().getActiveModList()) {
			String modId = mod.getModId();
			if(!Utils.MOD_IDS.contains(modId) && !BACK_MOD_IDS.contains(modId)) {
				Utils.MOD_IDS.add(modId);
			}
		}
		Utils.MOD_IDS.addAll(BACK_MOD_IDS);

		moduleBlacklist.addAll(Arrays.asList(configFile.get(name, "moduleBlacklist", new String[0]).setRequiresMcRestart(true).getStringList()));

		ingot = configFile.get(name, "ingot", true).setRequiresMcRestart(true).getBoolean();
		gem = configFile.get(name, "gem", true).setRequiresMcRestart(true).getBoolean();
		dust = configFile.get(name, "dust", true).setRequiresMcRestart(true).getBoolean();
		ingot_oreless = configFile.get(name, "ingot_oreless", false).setRequiresMcRestart(true).getBoolean();
		gem_oreless = configFile.get(name, "gem_oreless", false).setRequiresMcRestart(true).getBoolean();

		reloadColors = configFile.get(name, "reloadColorConfigs", false, "Set to true to reload color configs.").setRequiresMcRestart(true).getBoolean();

		if(reloadColors) {
			configFile.getCategory(name).remove("reloadColorConfigs");
			configFile.get(name, "reloadColorConfigs", false, "Set to true to reload color configs.").setRequiresMcRestart(true).getBoolean();
		}

		usedCategories.add(name);

		if(configFile.hasChanged())
			configFile.save();
	}

	public static void initOreConfigs(List<OreEntry> allOres) {
		for(OreEntry entry : allOres) {
			String name = Utils.to_under_score(entry.getOreName());

			if(entry.getOreType().ordinal() < 3) {
				String originalExtra = entry.getExtra();
				String configExtra = configFile.get(name, "extra", originalExtra).setRequiresMcRestart(true).getString();
				boolean doesOreExist = Utils.doesOreNameExist("ore"+configExtra);

				if(doesOreExist) {
					entry.setExtra(configExtra);
				}
				else {
					JAOPCAApi.LOGGER.warn("Found invalid extra name in ore entry "+entry.getOreName()+", replacing");
					configFile.getCategory(name).remove("extra");
					configFile.get(name, "extra", originalExtra).setRequiresMcRestart(true);
					entry.setExtra(originalExtra);
				}

				String originalExtra2 = entry.getExtra();
				String configExtra2 = configFile.get(name, "extra2", originalExtra2).setRequiresMcRestart(true).getString();
				boolean doesOreExist2 = Utils.doesOreNameExist("ore"+configExtra2);

				if(doesOreExist2) {
					entry.setSecondExtra(configExtra2);
				}
				else {
					JAOPCAApi.LOGGER.warn("Found invalid extra name in ore entry "+entry.getOreName()+", replacing");
					configFile.getCategory(name).remove("extra2");
					configFile.get(name, "extra2", originalExtra2).setRequiresMcRestart(true);
					entry.setExtra(originalExtra2);
				}
			}

			entry.setEnergyModifier(configFile.get(name, "energyModifier", entry.getEnergyModifier()).setRequiresMcRestart(true).getDouble());
			entry.setRarity(configFile.get(name, "rarity", entry.getRarity()).setRequiresMcRestart(true).getDouble());
			entry.addBlacklistedModules(Arrays.asList(configFile.get(name, "moduleBlacklist", new String[0]).setRequiresMcRestart(true).getStringList()));

			usedCategories.add(name);
		}

		if(configFile.hasChanged())
			configFile.save();
	}

	public static void preInitModulewiseConfigs() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			module.registerConfigsPre(configFile);
		}

		if(configFile.hasChanged())
			configFile.save();
	}

	public static void initModulewiseConfigs() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			module.registerConfigs(configFile);
		}

		if(configFile.hasChanged())
			configFile.save();
	}

	public static void initColorConfigs(OreEntry entry) {
		String name = Utils.to_under_score(entry.getOreName());

		if(reloadColors) {
			configFile.getCategory(name).remove("color");
		}

		entry.setColor(Color.decode(configFile.get(name, "color", "0x"+Integer.toHexString(entry.getColor() & 0xFFFFFF)).getString()));

		if(configFile.hasChanged())
			configFile.save();
	}
}
