package thelm.jaopca.materials;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.ApiImpl;

public class MaterialHandler {

	private MaterialHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeSet<String> BLACKLISTED_NAMES = new TreeSet<>();
	private static final TreeSet<String> USED_PREFIXES = new TreeSet<>(Arrays.asList(
			"ingotAny", "ingotHot", "ingotDouble", "ingotTriple", "ingotQuadruple", "ingotQuintuple",
			"gemAny", "gemOre", "gemRaw", "gemUncut", "gemPolished", "gemChipped", "gemFlawed", "gemFlawless", "gemExquisite", "gemLegendary",
			"crystalAny", "crystalFragment", "crystalShard", "crystalCluster", "crystalPure",
			"dustAny", "dustSmall", "dustTiny", "dustDirty", "dustDiv72", "dustImpure", "dustPure", "dustRefined", "dustRegular"));
	private static final ListMultimap<String, String> ALTERNATIVE_NAMES = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<String, Material> MATERIALS = new TreeMap<>();

	public static Map<String, Material> getMaterialMap() {
		return MATERIALS;
	}

	public static Collection<Material> getMaterials() {
		return MATERIALS.values();
	}

	public static Material getMaterial(String name) {
		return MATERIALS.get(name);
	}

	public static boolean containsMaterial(String name) {
		return MATERIALS.containsKey(name);
	}

	public static boolean registerBlacklistedMaterialNames(String... names) {
		return Collections.addAll(BLACKLISTED_NAMES, names);
	}

	public static boolean registerUsedPlainPrefixes(String... prefixes) {
		return Collections.addAll(USED_PREFIXES, prefixes);
	}

	public static boolean registerMaterialAlternativeNames(String name, String... alternatives) {
		return ALTERNATIVE_NAMES.putAll(name, Arrays.asList(alternatives));
	}

	public static void findMaterials() {
		MATERIALS.clear();

		Set<String> oredict = ApiImpl.INSTANCE.getOredict();

		Set<String> allMaterials = new TreeSet<>();

		Set<String> nameBlacklist = new TreeSet<>();

		Set<String> nonPlainPrefixes = ConfigHandler.nonPlainUsedPrefix ? USED_PREFIXES : Collections.emptySet();

		nameBlacklist.addAll(BLACKLISTED_NAMES);
		nameBlacklist.addAll(ConfigHandler.GEM_OVERRIDES);
		nameBlacklist.addAll(ConfigHandler.CRYSTAL_OVERRIDES);
		nameBlacklist.addAll(ConfigHandler.DUST_OVERRIDES);
		Set<String> ingots = ConfigHandler.ingot ? find(oredict, nameBlacklist, nonPlainPrefixes, "ingot", "ore") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(ingots);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		nameBlacklist.addAll(ConfigHandler.CRYSTAL_OVERRIDES);
		nameBlacklist.addAll(ConfigHandler.DUST_OVERRIDES);
		Set<String> gems = ConfigHandler.gem ? find(oredict, nameBlacklist, nonPlainPrefixes, "gem", "ore") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(gems);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		nameBlacklist.addAll(ConfigHandler.DUST_OVERRIDES);
		Set<String> crystals = ConfigHandler.crystal ? find(oredict, nameBlacklist, nonPlainPrefixes, "crystal", "ore") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(crystals);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		Set<String> dusts = ConfigHandler.dust ? find(oredict, nameBlacklist, nonPlainPrefixes, "dust", "ore") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(dusts);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		nameBlacklist.addAll(ConfigHandler.GEM_OVERRIDES);
		nameBlacklist.addAll(ConfigHandler.CRYSTAL_OVERRIDES);
		nameBlacklist.addAll(ConfigHandler.DUST_OVERRIDES);
		Set<String> ingotsPlain = ConfigHandler.ingotPlain ? find(oredict, nameBlacklist, USED_PREFIXES, "ingot") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(ingotsPlain);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		nameBlacklist.addAll(ConfigHandler.CRYSTAL_OVERRIDES);
		nameBlacklist.addAll(ConfigHandler.DUST_OVERRIDES);
		Set<String> gemsPlain = ConfigHandler.gemPlain ? find(oredict, nameBlacklist, USED_PREFIXES, "gem") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(gemsPlain);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		nameBlacklist.addAll(ConfigHandler.DUST_OVERRIDES);
		Set<String> crystalsPlain = ConfigHandler.crystalPlain ? find(oredict, nameBlacklist, USED_PREFIXES, "crystal") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(crystalsPlain);

		nameBlacklist.addAll(allMaterials);
		nameBlacklist.addAll(BLACKLISTED_NAMES);
		Set<String> dustsPlain = ConfigHandler.dustPlain ? find(oredict, nameBlacklist, USED_PREFIXES, "dust") : new LinkedHashSet<>();
		nameBlacklist.clear();
		allMaterials.addAll(dustsPlain);

		for(String name : ingots) {
			Material material = new Material(name, MaterialType.INGOT);
			MATERIALS.put(name, material);
			LOGGER.debug("Added ingot material {}", name);
		}
		for(String name : gems) {
			Material material = new Material(name, MaterialType.GEM);
			MATERIALS.put(name, material);
			LOGGER.debug("Added gem material {}", name);
		}
		for(String name : crystals) {
			Material material = new Material(name, MaterialType.CRYSTAL);
			MATERIALS.put(name, material);
			LOGGER.debug("Added crystal material {}", name);
		}
		for(String name : dusts) {
			Material material = new Material(name, MaterialType.DUST);
			MATERIALS.put(name, material);
			LOGGER.debug("Added dust material {}", name);
		}
		for(String name : ingotsPlain) {
			Material material = new Material(name, MaterialType.INGOT_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain ingot material {}", name);
		}
		for(String name : gemsPlain) {
			Material material = new Material(name, MaterialType.GEM_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain gem material {}", name);
		}
		for(String name : crystalsPlain) {
			Material material = new Material(name, MaterialType.CRYSTAL_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain crystal material {}", name);
		}
		for(String name : dustsPlain) {
			Material material = new Material(name, MaterialType.DUST_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain dust material {}", name);
		}
		LOGGER.info("Added {} materials", MATERIALS.size());
	}

	protected static Set<String> find(Set<String> entries, Set<String> nameBlacklist, Set<String> prefixBlacklist, String mainPrefix, String... prefixes) {
		Set<String> found = new TreeSet<>();
		for(String entry : entries) {
			if(entry.startsWith(mainPrefix)) {
				String name = entry.substring(mainPrefix.length());
				if(!name.isEmpty() && !Character.isLowerCase(name.charAt(0)) && !nameBlacklist.contains(name) && Arrays.stream(prefixes).map(prefix->prefix+name).allMatch(entries::contains)) {
					found.add(name);
				}
			}
		}
		Set<String> ret = new TreeSet<>();
		for(String name : found) {
			String entry = mainPrefix+name;
			if(prefixBlacklist.stream().noneMatch(bp->{
				if(entry.startsWith(bp)) {
					String nName = entry.substring(bp.length());
					return ConfigHandler.strictUsedPrefix || found.contains(nName) || nameBlacklist.contains(nName);
				}
				return false;
			})) {
				ret.add(name);
			}
		}
		return ret;
	}
}
