package thelm.jaopca.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.TreeMultimap;

import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.packs.ModFileResourcePack;

public class DataCollector {

	private DataCollector() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int TAGS_PATH_LENGTH = "tags/".length();
	private static final int RECIPES_PATH_LENGTH = "recipes/".length();
	private static final int ADVANCEMENTS_PATH_LENGTH = "advancements/".length();
	private static final int JSON_EXTENSION_LENGTH = ".json".length();
	private static final List<IResourcePack> RESOURCE_PACKS = new ArrayList<>();
	private static final TreeMultimap<String, ResourceLocation> DEFINED_TAGS = TreeMultimap.create();
	private static final TreeSet<ResourceLocation> DEFINED_RECIPES = new TreeSet<>();
	private static final TreeSet<ResourceLocation> DEFINED_ADVANCEMENTS = new TreeSet<>();

	public static void collectData() {
		DEFINED_TAGS.clear();
		DEFINED_RECIPES.clear();
		DEFINED_ADVANCEMENTS.clear();
		if(RESOURCE_PACKS.isEmpty()) {
			RESOURCE_PACKS.add(new VanillaPack("minecraft"));
			ModList.get().getModFiles().stream().
			map(mf->new ModFileResourcePack(mf.getFile())).
			forEach(RESOURCE_PACKS::add);
			/*
			 * Fabric:
			 * ModResourcePackUtil.appendModResourcePacks(RESOURCE_PACKS, ResourcePackType.SERVER_DATA);
			 */
		}
		for(ResourceLocation location : getAllDataResourceLocations("tags", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			path = path.substring(TAGS_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
			String[] split = path.split("/", 2);
			String type = split[0];
			path = split[1];
			DEFINED_TAGS.put(type, new ResourceLocation(namespace, path));
		}
		LOGGER.info("Found {} unique defined tags", DEFINED_TAGS.size());
		for(ResourceLocation location : getAllDataResourceLocations("recipes", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			if(!path.equals("recipes/_constants.json") && !path.equals("recipes/_factories.json")) {
				path = path.substring(RECIPES_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
				DEFINED_RECIPES.add(new ResourceLocation(namespace, path));
			}
		}
		LOGGER.info("Found {} unique defined recipes", DEFINED_RECIPES.size());
		for(ResourceLocation location : getAllDataResourceLocations("advancements", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			path = path.substring(RECIPES_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
			DEFINED_ADVANCEMENTS.add(new ResourceLocation(namespace, path));
		}
		LOGGER.info("Found {} unique defined advancements", DEFINED_ADVANCEMENTS.size());
	}

	public static Set<ResourceLocation> getDefinedTags(String type) {
		return DEFINED_TAGS.get(type);
	}

	public static Set<ResourceLocation> getDefinedRecipes() {
		return DEFINED_RECIPES;
	}

	public static Set<ResourceLocation> getDefinedAdvancements() {
		return DEFINED_ADVANCEMENTS;
	}

	static Collection<ResourceLocation> getAllDataResourceLocations(String pathIn, Predicate<String> filter) {
		List<ResourceLocation> list = new ArrayList<>();
		for(IResourcePack resourcePack : RESOURCE_PACKS) {
			list.addAll(resourcePack.getAllResourceLocations(ResourcePackType.SERVER_DATA, pathIn, Integer.MAX_VALUE, filter));
		}
		Collections.sort(list);
		return list;
	}
}
