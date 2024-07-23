package thelm.jaopca.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import thelm.jaopca.api.data.IDataModule;
import thelm.jaopca.api.data.JAOPCADataModule;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;
import thelm.jaopca.utils.MiscHelper;

public class DataInjector {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Type JAOPCA_DATA_MODULE = Type.getType(JAOPCADataModule.class);
	private static final Map<Class<?>, Consumer<Object>> RELOAD_INJECTORS = new HashMap<>();
	private static final LoadingCache<ResourceKey<? extends Registry<?>>, ListMultimap<ResourceLocation, ResourceLocation>> TAGS_INJECT = CacheBuilder.newBuilder().build(CacheLoader.from(()->MultimapBuilder.treeKeys().arrayListValues().build()));
	private static final TreeMap<ResourceLocation, IRecipeSerializer> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<LootTable>> LOOT_TABLES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<Advancement.Builder>> ADVANCEMENTS_INJECT = new TreeMap<>();

	public static void init() {
		RELOAD_INJECTORS.put(RecipeManager.class, DataInjector::injectRecipes);
	}

	public static boolean registerReloadInjector(Class<?> clazz, Consumer<Object> injector) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(injector);
		return RELOAD_INJECTORS.putIfAbsent(clazz, injector) == null;
	}

	public static boolean registerTag(ResourceKey<? extends Registry<?>> registry, ResourceLocation tagLocation, ResourceLocation objLocation) {
		Objects.requireNonNull(registry);
		Objects.requireNonNull(tagLocation);
		Objects.requireNonNull(objLocation);
		return TAGS_INJECT.getUnchecked(registry).put(tagLocation, objLocation);
	}

	public static boolean registerRecipe(ResourceLocation location, IRecipeSerializer recipeSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(recipeSupplier);
		return RECIPES_INJECT.putIfAbsent(location, recipeSupplier) == null;
	}

	public static boolean registerLootTable(ResourceLocation location, Supplier<LootTable> lootTableSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(lootTableSupplier);
		return LOOT_TABLES_INJECT.putIfAbsent(location, lootTableSupplier) == null;
	}

	public static boolean registerAdvancement(ResourceLocation location, Supplier<Advancement.Builder> advancementBuilder) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(advancementBuilder);
		return ADVANCEMENTS_INJECT.putIfAbsent(location, advancementBuilder) == null;
	}

	public static Set<ResourceLocation> getInjectTags(ResourceKey<? extends Registry<?>> registry) {
		Objects.requireNonNull(registry);
		return TAGS_INJECT.getUnchecked(registry).keySet();
	}

	public static Set<ResourceLocation> getInjectRecipes() {
		return RECIPES_INJECT.navigableKeySet();
	}

	public static Set<ResourceLocation> getInjectLootTables() {
		return LOOT_TABLES_INJECT.navigableKeySet();
	}

	public static Set<ResourceLocation> getInjectAdvancements() {
		return ADVANCEMENTS_INJECT.navigableKeySet();
	}

	public static void findDataModules() {
		Map<String, IDataModule> dataModules = new TreeMap<>();
		List<AnnotationData> annotationData = ModList.get().getAllScanData().stream().
				flatMap(data->data.getAnnotations().stream()).
				filter(data->JAOPCA_DATA_MODULE.equals(data.annotationType())).toList();
		Predicate<String> modVersionNotLoaded = MiscHelper.INSTANCE.modVersionNotLoaded(LOGGER);
		Predicate<String> classNotExists = MiscHelper.INSTANCE::classNotExists;
		for(AnnotationData aData : annotationData) {
			List<String> modDeps = (List<String>)aData.annotationData().get("modDependencies");
			List<String> classDeps = (List<String>)aData.annotationData().get("classDependencies");
			String className = aData.clazz().getClassName();
			if(modDeps != null && modDeps.stream().filter(Predicates.notNull()).anyMatch(modVersionNotLoaded)) {
				LOGGER.info("Data module {} has missing mod dependencies, skipping", className);
				continue;
			}
			if(classDeps != null && classDeps.stream().filter(Predicates.notNull()).anyMatch(classNotExists)) {
				LOGGER.info("Data module {} has missing class dependencies, skipping", className);
				continue;
			}
			try {
				Class<?> moduleClass = Class.forName(className);
				Class<? extends IDataModule> moduleInstanceClass = moduleClass.asSubclass(IDataModule.class);
				IDataModule module;
				try {
					Method method = moduleClass.getMethod("getInstance");
					module = (IDataModule)method.invoke(null);
				}
				catch(NoSuchMethodException | InvocationTargetException e) {
					module = moduleInstanceClass.newInstance();
				}
				if(ConfigHandler.DATA_MODULE_BLACKLIST.contains(module.getName())) {
					LOGGER.info("Data module {} is disabled in config, skipping", module.getName());
				}
				if(dataModules.putIfAbsent(module.getName(), module) != null) {
					LOGGER.fatal("Data module name conflict: {} for {} and {}", module.getName(), dataModules.get(module.getName()).getClass(), module.getClass());
					continue;
				}
				LOGGER.debug("Loaded data module {}", module.getName());
			}
			catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.fatal("Unable to load data module {}", className, e);
			}
		}
		for(IDataModule module : dataModules.values()) {
			module.register();
		}
	}

	public static void reloadInject(Class<?> clazz, Object object) {
		for(Class<?> c : ClassUtils.hierarchy(clazz, ClassUtils.Interfaces.INCLUDE)) {
			if(RELOAD_INJECTORS.containsKey(c)) {
				RELOAD_INJECTORS.get(c).accept(object);
				return;
			}
		}
	}

	public static void injectRecipes(Object object) {
		Map<ResourceLocation, JsonElement> recipeMap = (Map<ResourceLocation, JsonElement>)object;
		Map<ResourceLocation, JsonElement> recipesToInject = new TreeMap<>();
		RECIPES_INJECT.forEach((key, supplier)->{
			if(recipeMap.containsKey(key)) {
				LOGGER.debug("Duplicate recipe ignored with ID {}", key);
				return;
			}
			JsonElement recipe = null;
			try {
				recipe = supplier.get();
			}
			catch(IllegalArgumentException e) {
				LOGGER.debug("Recipe with ID {} received invalid arguments: {}", key, e.getMessage());
				return;
			}
			catch(Throwable e) {
				LOGGER.warn("Recipe with ID {} errored", key, e);
				return;
			}
			if(recipe == null) {
				LOGGER.debug("Recipe with ID {} returned null", key);
				return;
			}
			recipesToInject.put(key, recipe);
			LOGGER.debug("Injected recipe with ID {}", key);
		});
		recipesToInject.forEach(recipeMap::putIfAbsent);
		LOGGER.info("Injected {} recipes, {} recipes total", recipesToInject.size(), recipeMap.size());
	}

	public static class PackFinder implements RepositorySource {

		public static final PackFinder INSTANCE = new PackFinder();

		@Override
		public void loadPacks(Consumer<Pack> packConsumer) {
			Pack packInfo = Pack.readMetaAndCreate("jaopca:inmemory", Component.literal("JAOPCA In Memory Resources"), true, BuiltInPackSource.fromName(packId->{
				InMemoryResourcePack pack = new InMemoryResourcePack(packId, true);
				TAGS_INJECT.asMap().forEach((registry, map)->{
					String path = TagManager.getTagDir(registry)+'/';
					map.asMap().forEach((tagLocation, objLocations)->{
						TagBuilder builder = TagBuilder.create();
						objLocations.forEach(l->builder.addOptionalElement(l));
						pack.putJson(PackType.SERVER_DATA, tagLocation.withPath(path+tagLocation.getPath()+".json"), serializeTag(builder));
					});
				});
				LOOT_TABLES_INJECT.forEach((location, supplier)->{
					pack.putJson(PackType.SERVER_DATA, location.withPath("loot_tables/"+location.getPath()+".json"), serializeLootTable(supplier.get()));
				});
				ADVANCEMENTS_INJECT.forEach((location, supplier)->{
					pack.putJson(PackType.SERVER_DATA, location.withPath("advancements/"+location.getPath()+".json"), serializeAdvancement(supplier.get()));
				});
				ModuleHandler.onCreateDataPack(pack);
				return pack;
			}), PackType.SERVER_DATA, Pack.Position.BOTTOM, PackSource.BUILT_IN);
			if(packInfo != null) {
				packConsumer.accept(packInfo);
			}
		}
	}

	public static JsonElement serializeTag(TagBuilder tagBuilder) {
		return TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(tagBuilder.build(), false, List.of())).getOrThrow(false, LOGGER::error);
	}

	public static JsonElement serializeLootTable(LootTable lootTable) {
		return LootTable.CODEC.encodeStart(JsonOps.INSTANCE, lootTable).getOrThrow(false, LOGGER::error);
	}

	public static JsonElement serializeAdvancement(Advancement.Builder advancementBuilder) {
		return Advancement.CODEC.encodeStart(JsonOps.INSTANCE, advancementBuilder.build(null).value()).getOrThrow(false, LOGGER::error);
	}
}
