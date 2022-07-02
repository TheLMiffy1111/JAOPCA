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

	public static boolean registerMaterialAlternativeNames(String name, String... alternatives) {
		return ALTERNATIVE_NAMES.putAll(name, Arrays.asList(alternatives));
	}

	public static void findMaterials() {
		MATERIALS.clear();

		Set<String> oredict = ApiImpl.INSTANCE.getOredict();

		Set<String> allMaterials = new TreeSet<>();

		Set<String> ingots = ConfigHandler.ingot ? findOredictEntriesWithPrefixes(oredict, "ingot", "ore") : new LinkedHashSet<>();
		ingots.removeAll(BLACKLISTED_NAMES);
		ingots.removeAll(ConfigHandler.GEM_OVERRIDES);
		ingots.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		ingots.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(ingots);

		Set<String> gems = ConfigHandler.gem ? findOredictEntriesWithPrefixes(oredict, "gem", "ore") : new LinkedHashSet<>();
		gems.removeAll(allMaterials);
		gems.removeAll(BLACKLISTED_NAMES);
		gems.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		gems.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(gems);

		Set<String> crystals = ConfigHandler.crystal ? findOredictEntriesWithPrefixes(oredict, "crystal", "ore") : new LinkedHashSet<>();
		crystals.removeAll(allMaterials);
		crystals.removeAll(BLACKLISTED_NAMES);
		crystals.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(crystals);

		Set<String> dusts = ConfigHandler.dust ? findOredictEntriesWithPrefixes(oredict, "dust", "ore") : new LinkedHashSet<>();
		dusts.removeAll(allMaterials);
		dusts.removeAll(BLACKLISTED_NAMES);
		allMaterials.addAll(dusts);

		Set<String> ingotsPlain = ConfigHandler.ingotPlain ? findOredictEntriesWithPrefixes(oredict, "ingot") : new LinkedHashSet<>();
		ingotsPlain.removeAll(allMaterials);
		ingotsPlain.removeAll(BLACKLISTED_NAMES);
		ingotsPlain.removeAll(ConfigHandler.GEM_OVERRIDES);
		ingotsPlain.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		ingotsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(ingotsPlain);

		Set<String> gemsPlain = ConfigHandler.gemPlain ? findOredictEntriesWithPrefixes(oredict, "gem") : new LinkedHashSet<>();
		gemsPlain.removeAll(allMaterials);
		gemsPlain.removeAll(BLACKLISTED_NAMES);
		gemsPlain.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		gemsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(gemsPlain);

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
		LOGGER.info("Added {} materials", MATERIALS.size());
	}

	protected static Set<String> findOredictEntriesWithPrefixes(Set<String> entries, String mainPrefix, String... prefixes) {
		Set<String> ret = new TreeSet<>();
		for(String entry : entries) {
			if(entry.startsWith(mainPrefix)) {
				String name = entry.substring(mainPrefix.length());
				if(Arrays.stream(prefixes).map(prefix->prefix+name).allMatch(entries::contains)) {
					ret.add(name);
				}
			}
		}
		return ret;
	}
}
