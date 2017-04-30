package thelm.jaopca.utils;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.ore.OreEntry;

public class JAOPCAConfig {

	public static Configuration configFile;
	public static ArrayList<String> usedCategories = Lists.<String>newArrayList();

	public static void init(File file) {
		configFile = new Configuration(file);
	}

	public static void initOreConfigs(List<OreEntry> allOres) {
		for(OreEntry entry : allOres) {
			String name = entry.getOreName();

			entry.setExtra(configFile.get(name, "extra", entry.getExtra()).setRequiresMcRestart(true).getString());
			entry.setEnergyModifier(configFile.get(name, "energyModifier", entry.getEnergyModifier()).setRequiresMcRestart(true).getDouble());
			entry.addBlacklistedModules(Arrays.asList(configFile.get(name, "moduleBlacklist", new String[0]).setRequiresMcRestart(true).getStringList()));

			usedCategories.add(name);
		}

		if(configFile.hasChanged())
			configFile.save();
	}

	public static void initColorConfigs(OreEntry entry) {
		String name = entry.getOreName();

		entry.setColor(Color.decode(configFile.get(name, "color", "0x"+Integer.toHexString(entry.getColor() & 0xFFFFFF)).getString()));

		if(configFile.hasChanged())
			configFile.save();
	}
}
