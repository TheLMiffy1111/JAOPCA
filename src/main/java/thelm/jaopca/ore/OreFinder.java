package thelm.jaopca.ore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.utils.JAOPCAConfig;

public class OreFinder {

	public static final HashMap<String, String> DEFAULT_EXTRAS = Maps.<String, String>newHashMap();
	public static final HashMap<String, Double> DEFAULT_ENERGY_MODIFIERS = Maps.<String, Double>newHashMap();

	static {
		DEFAULT_EXTRAS.put("Cobalt", "Iron");
		DEFAULT_EXTRAS.put("Ardite", "Gold");
		DEFAULT_EXTRAS.put("Aluminum", "Tin");
		DEFAULT_EXTRAS.put("Copper", "Gold");
		DEFAULT_EXTRAS.put("Tin", "Iron");
		DEFAULT_EXTRAS.put("Lead", "Silver");
		DEFAULT_EXTRAS.put("Iron", "Nickel");
		DEFAULT_EXTRAS.put("Silver", "Lead");
		DEFAULT_EXTRAS.put("Nickel", "Platinum");
		DEFAULT_EXTRAS.put("FzDarkIron", "Silver");
		
		DEFAULT_ENERGY_MODIFIERS.put("Cobalt", 3D);
		DEFAULT_ENERGY_MODIFIERS.put("Ardite", 3D);
		DEFAULT_ENERGY_MODIFIERS.put("FzDarkIron", 3D);
		DEFAULT_ENERGY_MODIFIERS.put("Osmium", 2D);
		DEFAULT_ENERGY_MODIFIERS.put("Tungsten", 2D);
	}

	public static void findOres() {
		LinkedHashSet<String> allOres = getMaterialsWithPrefixes("ore", "ingot");
		ArrayList<OreEntry> allEntries = Lists.<OreEntry>newArrayList();
		for(String name : allOres) {
			OreEntry entry = new OreEntry(name);
			if(DEFAULT_EXTRAS.containsKey(name) && !OreDictionary.getOres("ingot"+DEFAULT_EXTRAS.get(name)).isEmpty()) {
				entry.setExtra(DEFAULT_EXTRAS.get(name));
			}
			if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
				entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
			}
			allEntries.add(entry);
		}
		JAOPCAApi.ORE_ENTRY_LIST.addAll(allEntries);
		JAOPCAConfig.initOreConfigs(allEntries);
	}

	public static LinkedHashSet<String> getMaterialsWithPrefixes(String prefix1, String prefix2) {
		LinkedHashSet<String> ores = Sets.<String>newLinkedHashSet();
		for(String name : OreDictionary.getOreNames()) {
			if(name.startsWith(prefix1) && !OreDictionary.getOres(name).isEmpty()) {
				String oreName = name.substring(prefix1.length());
				for(String n : OreDictionary.getOreNames()) {
					if(n.equals(prefix2 + oreName) && !OreDictionary.getOres(n).isEmpty())
						ores.add(oreName);
				}
			}
		}

		return ores;
	}
}
