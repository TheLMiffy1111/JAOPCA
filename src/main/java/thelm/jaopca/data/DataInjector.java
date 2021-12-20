package thelm.jaopca.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootSerializers;
import net.minecraft.loot.LootTable;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

public class DataInjector extends ReloadListener<Object> {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ListMultimap<ResourceLocation, ResourceLocation> BLOCK_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> ITEM_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> FLUID_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> ENTITY_TYPE_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<ResourceLocation, Supplier<? extends IRecipe<?>>> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<LootTable>> LOOT_TABLES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<Advancement.Builder>> ADVANCEMENTS_INJECT = new TreeMap<>();
	private static final Gson GSON = LootSerializers.func_237388_c_().create();

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

	public static boolean registerRecipe(ResourceLocation location, Supplier<? extends IRecipe<?>> recipeSupplier) {
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

	public static DataInjector getNewInstance(RecipeManager recipeManager) {
		return new DataInjector(recipeManager);
	}

	private final RecipeManager recipeManager;

	private DataInjector(RecipeManager recipeManager) {
		this.recipeManager = recipeManager;
	}

	@Override
	protected Object prepare(IResourceManager resourceManager, IProfiler profiler) {
		return null;
	}

	@Override
	protected void apply(Object splashList, IResourceManager resourceManager, IProfiler profiler) {
		injectRecipes(resourceManager);
	}

	public void injectRecipes(IResourceManager resourceManager) {
		List<IRecipe<?>> recipesToInject = new ArrayList<>();
		for(Map.Entry<ResourceLocation, Supplier<? extends IRecipe<?>>> entry : RECIPES_INJECT.entrySet()) {
			IRecipe recipe = null;
			try {
				recipe = entry.getValue().get();
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Recipe with ID {} received invalid arguments: {}", entry.getKey(), e.getMessage());
				continue;
			}
			catch(Throwable e) {
				LOGGER.error("Recipe with ID {} errored", entry.getKey(), e);
				continue;
			}
			if(recipe == null) {
				LOGGER.debug("Recipe with ID {} returned null", entry.getKey());
			}
			else if(!recipe.getId().equals(entry.getKey())) {
				LOGGER.warn("Recipe ID {} and registry key {} do not match", recipe.getId(), entry.getKey());
			}
			else if(recipeManager.getKeys().anyMatch(entry.getKey()::equals)) {
				LOGGER.warn("Duplicate recipe ignored with ID {}", entry.getKey());
			}
			else {
				recipesToInject.add(recipe);
			}
		}
		Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> recipesCopy =
				recipeManager.recipes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry->ImmutableMap.<ResourceLocation, IRecipe<?>>builder().putAll(entry.getValue())));
		for(IRecipe<?> recipe : recipesToInject) {
			recipesCopy.computeIfAbsent(recipe.getType(), type->ImmutableMap.builder()).put(recipe.getId(), recipe);
		}
		recipeManager.recipes = recipesCopy.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry->entry.getValue().build()));
		LOGGER.info("Injected {} recipes, {} recipes total", recipesToInject.size(), recipeManager.getKeys().count());
		ModuleHandler.onRecipeInjectComplete(resourceManager);
	}

	public static class PackFinder implements IPackFinder {

		public static final PackFinder INSTANCE = new PackFinder();

		@Override
		public void findPacks(Consumer<ResourcePackInfo> packList, ResourcePackInfo.IFactory factory) {
			ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				BLOCK_TAGS_INJECT.asMap().forEach((location, locations)->{
					ITag.Builder builder = ITag.Builder.create();
					locations.forEach(l->builder.addItemEntry(l, "inmemory:jaopca"));
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), builder.serialize());
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, locations)->{
					ITag.Builder builder = ITag.Builder.create();
					locations.forEach(l->builder.addItemEntry(l, "inmemory:jaopca"));
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/items/"+location.getPath()+".json"), builder.serialize());
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, locations)->{
					ITag.Builder builder = ITag.Builder.create();
					locations.forEach(l->builder.addItemEntry(l, "inmemory:jaopca"));
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), builder.serialize());
				});
				ENTITY_TYPE_TAGS_INJECT.asMap().forEach((location, locations)->{
					ITag.Builder builder = ITag.Builder.create();
					locations.forEach(l->builder.addItemEntry(l, "inmemory:jaopca"));
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/entity_types/"+location.getPath()+".json"), builder.serialize());
				});
				LOOT_TABLES_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "loot_tables/"+location.getPath()+".json"), GSON.toJsonTree(supplier.get()));
				});
				ADVANCEMENTS_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "advancements/"+location.getPath()+".json"), supplier.get().serialize());
				});
				ModuleHandler.onCreateDataPack(pack);
				return pack;
			}, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILTIN);
			if(packInfo != null) {
				packList.accept(packInfo);
			}
		}
	}
}
