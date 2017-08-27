package thelm.jaopca.ore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.utils.JAOPCAConfig;

public class OreFinder {

	public static final HashMap<String, String> DEFAULT_EXTRAS = Maps.<String, String>newHashMap();
	public static final HashMap<String, String> DEFAULT_SECOND_EXTRAS = Maps.<String, String>newHashMap();
	public static final HashMap<String, Double> DEFAULT_ENERGY_MODIFIERS = Maps.<String, Double>newHashMap();

	static {
		DEFAULT_EXTRAS.put("Cobalt", "Iron");
		DEFAULT_EXTRAS.put("Ardite", "Gold");
		DEFAULT_EXTRAS.put("Aluminum", "Iron");
		DEFAULT_EXTRAS.put("Copper", "Gold");
		DEFAULT_EXTRAS.put("Tin", "Iron");
		DEFAULT_EXTRAS.put("Lead", "Silver");
		DEFAULT_EXTRAS.put("Iron", "Nickel");
		DEFAULT_EXTRAS.put("Silver", "Lead");
		DEFAULT_EXTRAS.put("Nickel", "Platinum");
		DEFAULT_EXTRAS.put("Platinum", "Iridium");
		DEFAULT_EXTRAS.put("Iridium", "Platinum");
		DEFAULT_EXTRAS.put("Mithril", "Gold");
		DEFAULT_EXTRAS.put("FzDarkIron", "Silver");
	}

	public static void findOres() {
		JAOPCAApi.LOGGER.debug("Finding ores");

		ArrayList<OreEntry> allEntries = Lists.<OreEntry>newArrayList();
		LinkedHashSet<String> allOres = Sets.<String>newLinkedHashSet();

		LinkedHashSet<String> ingotOres = getMaterialsWithPrefixes("ore", "ingot");
		allOres.addAll(ingotOres);
		if(JAOPCAConfig.ingot) {
			for(String name : ingotOres) {
				OreEntry entry = new OreEntry(name);
				if(DEFAULT_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_EXTRAS.get(name));
				}
				if(DEFAULT_SECOND_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_SECOND_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_SECOND_EXTRAS.get(name));
				}
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
			}
		}

		LinkedHashSet<String> gemOres = getMaterialsWithPrefixes("ore", "gem");
		gemOres.removeAll(allOres);
		allOres.addAll(gemOres);
		if(JAOPCAConfig.gem) {
			for(String name : gemOres) {
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.GEM);
				if(DEFAULT_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_EXTRAS.get(name));
				}
				if(DEFAULT_SECOND_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_SECOND_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_SECOND_EXTRAS.get(name));
				}
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
			}
		}

		LinkedHashSet<String> dustOres = getMaterialsWithPrefixes("ore", "dust");
		dustOres.removeAll(allOres);
		allOres.addAll(dustOres);
		if(JAOPCAConfig.dust) {
			for(String name : dustOres) {
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.DUST);
				if(DEFAULT_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_EXTRAS.get(name));
				}
				if(DEFAULT_SECOND_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_SECOND_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_SECOND_EXTRAS.get(name));
				}
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
			}
		}

		LinkedHashSet<String> ingotNoOres = getMaterialsWithPrefixButNot("ingot", "ore");
		ingotNoOres.removeAll(allOres);
		allOres.addAll(ingotNoOres);
		if(JAOPCAConfig.ingot_oreless) {
			for(String name : ingotNoOres) {
				if(name.contains("Brick")) {
					continue;
				}
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.INGOT_ORELESS);
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
			}
		}

		LinkedHashSet<String> gemNoOres = getMaterialsWithPrefixButNot("gem", "ore");
		gemNoOres.removeAll(allOres);
		allOres.addAll(gemNoOres);
		if(JAOPCAConfig.gem_oreless) {
			for(String name : gemNoOres) {
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.GEM_ORELESS);
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
			}
		}

		JAOPCAApi.ORE_ENTRY_LIST.addAll(allEntries);
		for(EnumOreType type : EnumOreType.values()) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			allEntries.stream().
			filter(oreEntry->type == oreEntry.getOreType()).
			forEach(oreEntry->oreSet.add(oreEntry));

			JAOPCAApi.ORE_TYPE_TO_ORES_MAP.putAll(type, oreSet);
		}
		JAOPCAConfig.initOreConfigs(allEntries);
	}

	public static LinkedHashSet<String> getMaterialsWithPrefixes(String prefix1, String prefix2) {
		LinkedHashSet<String> ores = Sets.<String>newLinkedHashSet();
		for(String name : OreDictionary.getOreNames()) {
			if(name.startsWith(prefix1) && Utils.doesOreNameExist(name)) {
				String oreName = name.substring(prefix1.length());
				if(Utils.doesOreNameExist(prefix2 + oreName)) {
					ores.add(oreName);
				}
			}
		}

		return ores;
	}

	public static LinkedHashSet<String> getMaterialsWithPrefixButNot(String prefix1, String prefix2) {
		LinkedHashSet<String> ores = Sets.<String>newLinkedHashSet();
		for(String name : OreDictionary.getOreNames()) {
			if(name.startsWith(prefix1) && Utils.doesOreNameExist(name)) {
				String oreName = name.substring(prefix1.length());
				if(!Utils.doesOreNameExist(prefix2 + oreName)) {
					ores.add(oreName);
				}
			}
		}

		return ores;
	}
}
