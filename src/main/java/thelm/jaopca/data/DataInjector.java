package thelm.jaopca.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import com.google.common.base.Predicates;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
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
	private static final ListMultimap<ResourceLocation, ResourceLocation> BLOCK_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> ITEM_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> FLUID_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> ENTITY_TYPE_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<ResourceLocation, IRecipeSerializer> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<LootTable>> LOOT_TABLES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<Advancement.Builder>> ADVANCEMENTS_INJECT = new TreeMap<>();
	private static final Gson GSON = Deserializers.createLootTableSerializer().create();

	public static boolean registerBlockTag(ResourceLocation location, ResourceLocation blockLocation) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(blockLocation);
		return BLOCK_TAGS_INJECT.put(location, blockLocation);
	}

	public static boolean registerItemTag(ResourceLocation location, ResourceLocation itemLocation) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(itemLocation);
		return ITEM_TAGS_INJECT.put(location, itemLocation);
	}

	public static boolean registerFluidTag(ResourceLocation location, ResourceLocation fluidLocation) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(fluidLocation);
		return FLUID_TAGS_INJECT.put(location, fluidLocation);
	}

	public static boolean registerEntityTypeTag(ResourceLocation location, ResourceLocation entityTypeLocation) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(entityTypeLocation);
		return ENTITY_TYPE_TAGS_INJECT.put(location, entityTypeLocation);
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

	public static Set<ResourceLocation> getInjectBlockTags() {
		return BLOCK_TAGS_INJECT.keySet();
	}

	public static Set<ResourceLocation> getInjectItemTags() {
		return ITEM_TAGS_INJECT.keySet();
	}

	public static Set<ResourceLocation> getInjectFluidTags() {
		return FLUID_TAGS_INJECT.keySet();
	}

	public static Set<ResourceLocation> getInjectEntityTypeTags() {
		return ENTITY_TYPE_TAGS_INJECT.keySet();
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
		for(AnnotationData aData : annotationData) {
			List<String> deps = (List<String>)aData.annotationData().get("modDependencies");
			String className = aData.clazz().getClassName();
			if(deps != null && deps.stream().filter(Predicates.notNull()).anyMatch(modVersionNotLoaded)) {
				LOGGER.info("Data module {} has missing mod dependencies, skipping", className);
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

	public static void injectRecipes(Map<ResourceLocation, JsonElement> recipeMap) {
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
		public void loadPacks(Consumer<Pack> packConsumer, Pack.PackConstructor packConstructor) {
			Pack packInfo = Pack.create("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				BLOCK_TAGS_INJECT.asMap().forEach((location, locations)->{
					Tag.Builder builder = Tag.Builder.tag();
					locations.forEach(l->builder.addOptionalElement(l, "inmemory:jaopca"));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), builder.serializeToJson());
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, locations)->{
					Tag.Builder builder = Tag.Builder.tag();
					locations.forEach(l->builder.addOptionalElement(l, "inmemory:jaopca"));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/items/"+location.getPath()+".json"), builder.serializeToJson());
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, locations)->{
					Tag.Builder builder = Tag.Builder.tag();
					locations.forEach(l->builder.addOptionalElement(l, "inmemory:jaopca"));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), builder.serializeToJson());
				});
				ENTITY_TYPE_TAGS_INJECT.asMap().forEach((location, locations)->{
					Tag.Builder builder = Tag.Builder.tag();
					locations.forEach(l->builder.addOptionalElement(l, "inmemory:jaopca"));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/entity_types/"+location.getPath()+".json"), builder.serializeToJson());
				});
				LOOT_TABLES_INJECT.forEach((location, supplier)->{
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "loot_tables/"+location.getPath()+".json"), GSON.toJsonTree(supplier.get()));
				});
				ADVANCEMENTS_INJECT.forEach((location, supplier)->{
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "advancements/"+location.getPath()+".json"), supplier.get().serializeToJson());
				});
				ModuleHandler.onCreateDataPack(pack);
				return pack;
			}, packConstructor, Pack.Position.BOTTOM, PackSource.BUILT_IN);
			if(packInfo != null) {
				packConsumer.accept(packInfo);
			}
		}
	}
}
