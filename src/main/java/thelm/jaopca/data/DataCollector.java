package thelm.jaopca.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import com.google.common.base.Predicates;
import com.google.common.collect.TreeMultimap;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;
import thelm.jaopca.utils.MiscHelper;

public class DataCollector {

	private DataCollector() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int TAGS_PATH_LENGTH = "tags/".length();
	private static final FileToIdConverter TAG_FORMAT = FileToIdConverter.json("tags");
	private static final FileToIdConverter RECIPE_FORMAT = FileToIdConverter.json("recipe");
	private static final FileToIdConverter LOOT_TABLE_FORMAT = FileToIdConverter.json("loot_table");
	private static final FileToIdConverter ADVANCEMENT_FORMAT = FileToIdConverter.json("advancement");
	private static final Type JAOPCA_PACK_SUPPLIER = Type.getType(JAOPCAPackSupplier.class);
	private static final TreeMultimap<String, ResourceLocation> DEFINED_TAGS = TreeMultimap.create();
	private static final TreeSet<ResourceLocation> DEFINED_RECIPES = new TreeSet<>();
	private static final TreeSet<ResourceLocation> DEFINED_LOOT_TABLES = new TreeSet<>();
	private static final TreeSet<ResourceLocation> DEFINED_ADVANCEMENTS = new TreeSet<>();

	public static void collectData() {
		DEFINED_TAGS.clear();
		DEFINED_RECIPES.clear();
		DEFINED_ADVANCEMENTS.clear();

		PackLocationInfo emptyLocation = new PackLocationInfo("", Component.empty(), PackSource.DEFAULT, Optional.empty());
		List<PackResources> resourcePacks = new ArrayList<>();
		resourcePacks.add(ServerPacksSource.createVanillaPackSource());
		ModList.get().getModFiles().stream().
		map(mf->ResourcePackLoader.createPackForMod(mf).openPrimary(emptyLocation)).
		forEach(resourcePacks::add);
		List<AnnotationData> annotationData = ModList.get().getAllScanData().stream().
				flatMap(data->data.getAnnotations().stream()).
				filter(data->JAOPCA_PACK_SUPPLIER.equals(data.annotationType())).toList();
		Predicate<String> modVersionNotLoaded = MiscHelper.INSTANCE.modVersionNotLoaded(LOGGER);
		Predicate<String> classNotExists = MiscHelper.INSTANCE::classNotExists;
		for(AnnotationData aData : annotationData) {
			List<String> modDeps = (List<String>)aData.annotationData().get("modDependencies");
			List<String> classDeps = (List<String>)aData.annotationData().get("classDependencies");
			String className = aData.clazz().getClassName();
			if(modDeps != null && modDeps.stream().filter(Predicates.notNull()).anyMatch(modVersionNotLoaded)) {
				LOGGER.info("Pack supplier {} has missing mod dependencies, skipping", className);
				continue;
			}
			if(classDeps != null && classDeps.stream().filter(Predicates.notNull()).anyMatch(classNotExists)) {
				LOGGER.info("Pack supplier {} has missing class dependencies, skipping", className);
				continue;
			}
			try {
				Class<?> supplierClass = Class.forName(className);
				Class<? extends IPackSupplier> supplierInstanceClass = supplierClass.asSubclass(IPackSupplier.class);
				IPackSupplier supplier;
				try {
					Method method = supplierClass.getMethod("getInstance");
					supplier = (IPackSupplier)method.invoke(null);
				}
				catch(NoSuchMethodException | InvocationTargetException e) {
					supplier = supplierInstanceClass.newInstance();
				}
				supplier.addPacks(resourcePacks::add);
				LOGGER.debug("Loaded pack supplier {}", className);
			}
			catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.fatal("Unable to load pack supplier {}", className, e);
			}
		}

		try(MultiPackResourceManager resourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, resourcePacks)) {
			for(ResourceLocation location : TAG_FORMAT.listMatchingResources(resourceManager).keySet()) {
				location = TAG_FORMAT.fileToId(location);
				String namespace = location.getNamespace();
				String path = location.getPath();
				String[] split = path.split("/", 2);
				if(split.length == 2) {
					String type = split[0];
					if(ModList.get().isLoaded(type)) {
						String[] split0 = split[1].split("/", 2);
						if(split0.length == 2) {
							type += '/'+split0[0];
							path = split0[1];
							DEFINED_TAGS.put(type, location.withPath(path));
							continue;
						}
					}
					path = split[1];
					DEFINED_TAGS.put(type, location.withPath(path));
				}
				else {
					LOGGER.error("Tag {} in namespace {} has no type", path, namespace);
				}
			}
			LOGGER.info("Found {} unique defined tags", DEFINED_TAGS.size());
			for(ResourceLocation location : RECIPE_FORMAT.listMatchingResources(resourceManager).keySet()) {
				location = RECIPE_FORMAT.fileToId(location);
				if(!location.getPath().equals("_constants") && !location.getPath().equals("_factories")) {
					DEFINED_RECIPES.add(location);
				}
			}
			LOGGER.info("Found {} unique defined recipes", DEFINED_RECIPES.size());
			for(ResourceLocation location : LOOT_TABLE_FORMAT.listMatchingResources(resourceManager).keySet()) {
				DEFINED_LOOT_TABLES.add(LOOT_TABLE_FORMAT.fileToId(location));
			}
			LOGGER.info("Found {} unique defined loot tables", DEFINED_LOOT_TABLES.size());
			for(ResourceLocation location : ADVANCEMENT_FORMAT.listMatchingResources(resourceManager).keySet()) {
				DEFINED_ADVANCEMENTS.add(ADVANCEMENT_FORMAT.fileToId(location));
			}
			LOGGER.info("Found {} unique defined advancements", DEFINED_ADVANCEMENTS.size());
		}
	}

	public static Set<ResourceLocation> getDefinedTags(ResourceKey<? extends Registry<?>> registry) {
		return getDefinedTags(Registries.tagsDirPath(registry).substring(TAGS_PATH_LENGTH));
	}

	public static Set<ResourceLocation> getDefinedTags(String type) {
		return DEFINED_TAGS.get(type.replace(':', '/'));
	}

	public static Set<ResourceLocation> getDefinedRecipes() {
		return DEFINED_RECIPES;
	}

	public static Set<ResourceLocation> getDefinedLootTables() {
		return DEFINED_LOOT_TABLES;
	}

	public static Set<ResourceLocation> getDefinedAdvancements() {
		return DEFINED_ADVANCEMENTS;
	}
}
