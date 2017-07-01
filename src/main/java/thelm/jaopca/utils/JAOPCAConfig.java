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

	public static void init(File file) {
		configFile = new Configuration(file);
		initModConfigs();
	}

	public static void initModConfigs() {
		String name = "jaopca";

		Utils.MOD_IDS.addAll(Arrays.asList(configFile.get(name, "orePreference", new String[0]).setRequiresMcRestart(true).getStringList()));

		for(ModContainer mod : Loader.instance().getActiveModList()) {
			String modId = mod.getModId();
			if(!Utils.MOD_IDS.contains(modId)) {
				Utils.MOD_IDS.add(modId);
			}
		}

		moduleBlacklist.addAll(Arrays.asList(configFile.get(name, "moduleBlacklist", new String[0]).setRequiresMcRestart(true).getStringList()));

		usedCategories.add(name);

		if(configFile.hasChanged())
			configFile.save();
	}

	public static void initOreConfigs(List<OreEntry> allOres) {
		for(OreEntry entry : allOres) {
			String name = Utils.to_under_score(entry.getOreName());

			String originalExtra = entry.getExtra();
			String configExtra = configFile.get(name, "extra", originalExtra).setRequiresMcRestart(true).getString();
			boolean doesOreExist = Utils.doesOreNameExist("ore"+configExtra) && Utils.doesOreNameExist("ingot"+configExtra);

			if(doesOreExist) {
				entry.setExtra(configExtra);
			}
			else {
				JAOPCAApi.LOGGER.warn("Found invalid extra name in ore entry "+entry.getOreName()+", replacing");
				configFile.getCategory(name).remove("extra");
				configFile.get(name, "extra", originalExtra).setRequiresMcRestart(true);
				entry.setExtra(originalExtra);
			}

			entry.setEnergyModifier(configFile.get(name, "energyModifier", entry.getEnergyModifier()).setRequiresMcRestart(true).getDouble());
			entry.addBlacklistedModules(Arrays.asList(configFile.get(name, "moduleBlacklist", new String[0]).setRequiresMcRestart(true).getStringList()));

			usedCategories.add(name);
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

		entry.setColor(Color.decode(configFile.get(name, "color", "0x"+Integer.toHexString(entry.getColor() & 0xFFFFFF)).getString()));

		if(configFile.hasChanged())
			configFile.save();
	}
}
