package thelm.jaopca.materials;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.ApiImpl;

public class MaterialHandler {

	private MaterialHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
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

	public static void findMaterials() {
		MATERIALS.clear();

		TreeSet<String> tags = ApiImpl.INSTANCE.getItemTags().stream().map(ResourceLocation::toString).collect(Collectors.toCollection(TreeSet::new));

		Set<String> allMaterials = new TreeSet<>();

		Set<String> ingots = ConfigHandler.ingot ? findItemTagNamesWithPaths(tags, "forge:ingots/", "forge:ores/") : new LinkedHashSet<>();
		ingots.removeAll(ConfigHandler.GEM_OVERRIDES);
		ingots.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		ingots.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(ingots);

		Set<String> gems = ConfigHandler.gem ? findItemTagNamesWithPaths(tags, "forge:gems/", "forge:ores/") : new LinkedHashSet<>();
		gems.removeAll(allMaterials);
		gems.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		gems.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(gems);

		Set<String> crystals = ConfigHandler.crystal ? findItemTagNamesWithPaths(tags, "forge:crystals/", "forge:ores/") : new LinkedHashSet<>();
		crystals.removeAll(allMaterials);
		crystals.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(crystals);

		Set<String> dusts = ConfigHandler.dust ? findItemTagNamesWithPaths(tags, "forge:dusts/", "forge:ores/") : new LinkedHashSet<>();
		dusts.removeAll(allMaterials);
		allMaterials.addAll(dusts);

		Set<String> ingotsPlain = ConfigHandler.ingotPlain ? findItemTagNamesWithPaths(tags, "forge:ingots/") : new LinkedHashSet<>();
		ingotsPlain.removeAll(allMaterials);
		ingotsPlain.removeAll(ConfigHandler.GEM_OVERRIDES);
		ingotsPlain.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		ingotsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(ingotsPlain);

		Set<String> gemsPlain = ConfigHandler.gemPlain ? findItemTagNamesWithPaths(tags, "forge:gems/") : new LinkedHashSet<>();
		gemsPlain.removeAll(allMaterials);
		gemsPlain.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		gemsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(gemsPlain);

		Set<String> crystalsPlain = ConfigHandler.crystalPlain ? findItemTagNamesWithPaths(tags, "forge:crystals/") : new LinkedHashSet<>();
		crystalsPlain.removeAll(allMaterials);
		crystalsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(crystalsPlain);

		Set<String> dustsPlain = ConfigHandler.dustPlain ? findItemTagNamesWithPaths(tags, "forge:dusts/") : new LinkedHashSet<>();
		dustsPlain.removeAll(allMaterials);
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

	protected static Set<String> findItemTagNamesWithPaths(TreeSet<String> tags, String mainPath, String... paths) {
		Set<String> ret = new TreeSet<>();
		for(String tag : tags) {
			if(tag.startsWith(mainPath) && !tags.ceiling(tag+'/').startsWith(tag+'/')) {
				String name = tag.substring(mainPath.length());
				if(!name.contains("/") && Arrays.stream(paths).map(path->path+name).allMatch(tags::contains)) {
					ret.add(name);
				}
			}
		}
		return ret;
	}
}
