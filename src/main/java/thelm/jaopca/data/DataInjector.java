package thelm.jaopca.data;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

public class DataInjector {

	private static final Logger LOGGER = LogManager.getLogger();
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
					TagBuilder builder = TagBuilder.create();
					locations.forEach(l->builder.addOptionalElement(l));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), serializeTag(builder));
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, locations)->{
					TagBuilder builder = TagBuilder.create();
					locations.forEach(l->builder.addOptionalElement(l));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/items/"+location.getPath()+".json"), serializeTag(builder));
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, locations)->{
					TagBuilder builder = TagBuilder.create();
					locations.forEach(l->builder.addOptionalElement(l));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), serializeTag(builder));
				});
				ENTITY_TYPE_TAGS_INJECT.asMap().forEach((location, locations)->{
					TagBuilder builder = TagBuilder.create();
					locations.forEach(l->builder.addOptionalElement(l));
					pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/entity_types/"+location.getPath()+".json"), serializeTag(builder));
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

	public static JsonElement serializeTag(TagBuilder tagBuilder) {
		return TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(tagBuilder.build(), false)).getOrThrow(false, LOGGER::error);
	}
}
