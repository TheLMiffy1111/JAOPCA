package thelm.jaopca.ore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.utils.JAOPCAConfig;

public class OreFinder {

	public static final TreeMap<String, String> DEFAULT_EXTRAS = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
	public static final TreeMap<String, String> DEFAULT_SECOND_EXTRAS = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
	public static final TreeMap<String, String> DEFAULT_THIRD_EXTRAS = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
	public static final TreeMap<String, Double> DEFAULT_ENERGY_MODIFIERS = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
	public static final TreeMap<String, Double> DEFAULT_RARITIES = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
	public static final ArrayList<String> CONTAINING_BLACKLIST = Lists.newArrayList(
			"Aluminum",
			"Sulphur",
			"Chrome",
			"Cesium",
			"Wolfram", "Wolframium",

			"Brick"
			);
	public static final TreeSet<String> SHOULD_BE_GEMS = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
	public static final TreeSet<String> SHOULD_BE_DUSTS = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
	public static final ArrayList<String> PREFIX_BLACKLIST = Lists.newArrayList();
	public static final HashSet<String> CONFLICT_PRECEDENCE = Sets.newHashSet();

	static {
		DEFAULT_EXTRAS.put("Cobalt", "Iron");
		DEFAULT_EXTRAS.put("Ardite", "Gold");
		DEFAULT_EXTRAS.put("Aluminium", "Iron");
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

		ArrayList<OreEntry> allEntries = Lists.newArrayList();
		LinkedHashSet<String> allOres = Sets.newLinkedHashSet();

		TreeSet<String> ingotOres = getMaterialsWithPrefixes("ore", "ingot");
		ingotOres.removeAll(SHOULD_BE_GEMS);
		ingotOres.removeAll(SHOULD_BE_DUSTS);
		allOres.addAll(ingotOres);
		if(JAOPCAConfig.ingot) {
			main:for(String name : ingotOres) {
				for(String nonsense : CONTAINING_BLACKLIST) {
					if(name.contains(nonsense)) {
						continue main;
					}
				}
				OreEntry entry = new OreEntry(name);
				if(DEFAULT_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_EXTRAS.get(name));
				}
				if(DEFAULT_SECOND_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_SECOND_EXTRAS.get(name))) {
					entry.setSecondExtra(DEFAULT_SECOND_EXTRAS.get(name));
				}
				if(DEFAULT_THIRD_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_THIRD_EXTRAS.get(name))) {
					entry.setThirdExtra(DEFAULT_THIRD_EXTRAS.get(name));
				}
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
				JAOPCAApi.LOGGER.debug("Found ingot ore "+name);
			}
		}

		TreeSet<String> gemOres = getMaterialsWithPrefixes("ore", "gem");
		gemOres.removeAll(SHOULD_BE_DUSTS);
		gemOres.removeAll(allOres);
		allOres.addAll(gemOres);
		if(JAOPCAConfig.gem) {
			main:for(String name : gemOres) {
				for(String nonsense : CONTAINING_BLACKLIST) {
					if(name.contains(nonsense)) {
						continue main;
					}
				}
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.GEM);
				if(DEFAULT_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_EXTRAS.get(name));
				}
				if(DEFAULT_SECOND_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_SECOND_EXTRAS.get(name))) {
					entry.setSecondExtra(DEFAULT_SECOND_EXTRAS.get(name));
				}
				if(DEFAULT_THIRD_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_THIRD_EXTRAS.get(name))) {
					entry.setThirdExtra(DEFAULT_THIRD_EXTRAS.get(name));
				}
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
				JAOPCAApi.LOGGER.debug("Found gem ore "+name);
			}
		}

		TreeSet<String> dustOres = getMaterialsWithPrefixes("ore", "dust");
		dustOres.removeAll(allOres);
		allOres.addAll(dustOres);
		if(JAOPCAConfig.dust) {
			main:for(String name : dustOres) {
				for(String nonsense : CONTAINING_BLACKLIST) {
					if(name.contains(nonsense)) {
						continue main;
					}
				}
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.DUST);
				if(DEFAULT_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_EXTRAS.get(name))) {
					entry.setExtra(DEFAULT_EXTRAS.get(name));
				}
				if(DEFAULT_SECOND_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_SECOND_EXTRAS.get(name))) {
					entry.setSecondExtra(DEFAULT_SECOND_EXTRAS.get(name));
				}
				if(DEFAULT_THIRD_EXTRAS.containsKey(name) && Utils.doesOreNameExist("ore"+DEFAULT_THIRD_EXTRAS.get(name))) {
					entry.setThirdExtra(DEFAULT_THIRD_EXTRAS.get(name));
				}
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				if(DEFAULT_RARITIES.containsKey(name)) {
					entry.setRarity(DEFAULT_RARITIES.get(name));
				}
				allEntries.add(entry);
				JAOPCAApi.LOGGER.debug("Found dust ore "+name);
			}
		}

		TreeSet<String> ingotNoOres = getMaterialsWithPrefixButNot("ingot", "ore");
		ingotNoOres.removeAll(SHOULD_BE_GEMS);
		ingotNoOres.removeAll(allOres);
		allOres.addAll(ingotNoOres);
		if(JAOPCAConfig.ingot_oreless) {
			main:for(String name : ingotNoOres) {
				for(String nonsense : CONTAINING_BLACKLIST) {
					if(name.contains(nonsense)) {
						continue main;
					}
				}
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.INGOT_ORELESS);
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
				JAOPCAApi.LOGGER.debug("Found ingot "+name);
			}
		}

		TreeSet<String> gemNoOres = getMaterialsWithPrefixButNot("gem", "ore");
		gemNoOres.removeAll(allOres);
		allOres.addAll(gemNoOres);
		if(JAOPCAConfig.gem_oreless) {
			main:for(String name : gemNoOres) {
				for(String nonsense : CONTAINING_BLACKLIST) {
					if(name.contains(nonsense)) {
						continue main;
					}
				}
				OreEntry entry = new OreEntry(name);
				entry.setOreType(EnumOreType.GEM_ORELESS);
				if(DEFAULT_ENERGY_MODIFIERS.containsKey(name)) {
					entry.setEnergyModifier(DEFAULT_ENERGY_MODIFIERS.get(name));
				}
				allEntries.add(entry);
				JAOPCAApi.LOGGER.debug("Found gem "+name);
			}
		}

		JAOPCAApi.ORE_ENTRY_LIST.addAll(allEntries);
		for(EnumOreType type : EnumOreType.values()) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			allEntries.stream().
			filter(oreEntry->type == oreEntry.getOreType()).
			forEach(oreSet::add);

			JAOPCAApi.ORE_TYPE_TO_ORES_MAP.putAll(type, oreSet);
		}
		JAOPCAConfig.initOreConfigs(allEntries);
	}

	public static TreeSet<String> getMaterialsWithPrefixes(String prefix1, String prefix2) {
		TreeSet<String> ores = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
		for(String name : OreDictionary.getOreNames()) {
			if(name.startsWith(prefix1) && Utils.doesOreNameExist(name)) {
				String oreName = name.substring(prefix1.length());
				if(Utils.doesOreNameExist(prefix2 + oreName)) {
					if(!ores.contains(oreName)) {
						ores.add(oreName);
					}
					else {
						String oreNameInSet = ores.stream().filter(str->str.equalsIgnoreCase(oreName)).findAny().orElse(oreName);
						confictingSuffixReplace(oreNameInSet, oreName);
						ores.remove(oreNameInSet);
						ores.add(determinePrecedentSuffix(oreNameInSet, oreName));
					}
				}
			}
		}

		return ores;
	}

	public static TreeSet<String> getMaterialsWithPrefixButNot(String prefix1, String prefix2) {
		TreeSet<String> ores = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
		main:for(String name : OreDictionary.getOreNames()) {
			if(name.startsWith(prefix1) && Utils.doesOreNameExist(name)) {
				for(String prefixB : PREFIX_BLACKLIST) {
					if(name.startsWith(prefixB)) {
						String oreName = name.substring(prefixB.length());
						if(oreName.length() > 0 && Character.isUpperCase(oreName.charAt(0))) {
							continue main;
						}
					}
				}
				String oreName = name.substring(prefix1.length());
				if(!Utils.doesOreNameExist(prefix2 + oreName)) {
					if(!ores.contains(oreName)) {
						ores.add(oreName);
					}
					else {
						String oreNameInSet = ores.stream().filter(str->str.equalsIgnoreCase(oreName)).findAny().orElse(oreName);
						confictingSuffixReplace(oreNameInSet, oreName);
						ores.remove(oreNameInSet);
						ores.add(determinePrecedentSuffix(oreNameInSet, oreName));
					}
				}
			}
		}

		return ores;
	}

	public static String determinePrecedentSuffix(String suffix1, String suffix2) {
		int flag = 0;
		if(CONFLICT_PRECEDENCE.contains(suffix1)) {
			flag += 1;
		}
		if(CONFLICT_PRECEDENCE.contains(suffix2)) {
			flag += 2;
		}
		if(flag == 1) {
			return suffix1;
		}
		if(flag == 2) {
			return suffix2;
		}
		long count1 = suffix1.chars().filter(Character::isUpperCase).count();
		long count2 = suffix2.chars().filter(Character::isUpperCase).count();
		return count1 > count2 ? suffix1 : suffix2;
	}

	public static void confictingSuffixReplace(String suffix1, String suffix2) {
		for(String name : OreDictionary.getOreNames()) {
			if(name.endsWith(suffix1)) {
				String replaced = StringUtils.removeEnd(name, suffix1)+suffix2;
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.endsWith(suffix2)) {
				String replaced = StringUtils.removeEnd(name, suffix2)+suffix1;
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
		}
	}
}
