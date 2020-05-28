package thelm.jaopca.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.BinomialRange;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.IntClamper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryManager;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

public class DataInjector extends ReloadListener<Object> {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ListMultimap<ResourceLocation, Supplier<? extends Block>> BLOCK_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<? extends Item>> ITEM_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<? extends Fluid>> FLUID_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<? extends EntityType<?>>> ENTITY_TYPE_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<ResourceLocation, Supplier<? extends IRecipe<?>>> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<LootTable>> LOOT_TABLES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<Advancement.Builder>> ADVANCEMENTS_INJECT = new TreeMap<>();
	private static final Gson GSON = new GsonBuilder().
			registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).
			registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).
			registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).
			registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).
			registerTypeAdapter(LootPool.class, new LootPool.Serializer()).
			registerTypeAdapter(LootTable.class, new LootTable.Serializer()).
			registerTypeHierarchyAdapter(LootEntry.class, new LootEntryManager.Serializer()).
			registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).
			registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).
			registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).
			create();

	public static boolean registerBlockTag(ResourceLocation location, Supplier<? extends Block> blockSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(blockSupplier);
		return BLOCK_TAGS_INJECT.put(location, blockSupplier);
	}

	public static boolean registerItemTag(ResourceLocation location, Supplier<? extends Item> itemSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(itemSupplier);
		return ITEM_TAGS_INJECT.put(location, itemSupplier);
	}

	public static boolean registerFluidTag(ResourceLocation location, Supplier<? extends Fluid> fluidSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(fluidSupplier);
		return FLUID_TAGS_INJECT.put(location, fluidSupplier);
	}

	public static boolean registerEntityTypeTag(ResourceLocation location, Supplier<? extends EntityType<?>> entityTypeSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(entityTypeSupplier);
		return ENTITY_TYPE_TAGS_INJECT.put(location, entityTypeSupplier);
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
			if(recipe == null) {
				LOGGER.warn("Recipe with ID {} returned null", entry.getKey());
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
		public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory) {
			T packInfo = ResourcePackInfo.createResourcePack("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				BLOCK_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Block[] blocks = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(Block[]::new);
					Tag<Block> tag = Tag.Builder.<Block>create().add(blocks).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.BLOCKS::getKey));
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Item[] items = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(Item[]::new);
					Tag<Item> tag = Tag.Builder.<Item>create().add(items).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/items/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.ITEMS::getKey));
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Fluid[] fluids = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(Fluid[]::new);
					Tag<Fluid> tag = Tag.Builder.<Fluid>create().add(fluids).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.FLUIDS::getKey));
				});
				ENTITY_TYPE_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					EntityType<?>[] entityTypes = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(EntityType<?>[]::new);
					Tag<EntityType<?>> tag = Tag.Builder.<EntityType<?>>create().add(entityTypes).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/entity_types/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.ENTITIES::getKey));
				});
				LOOT_TABLES_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "loot_tables/"+location.getPath()+".json"), GSON.toJsonTree(supplier.get()));
				});
				ADVANCEMENTS_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "advancements/"+location.getPath()+".json"), supplier.get().serialize());
				});
				ModuleHandler.onCreateDataPack(pack);
				return pack;
			}, factory, ResourcePackInfo.Priority.BOTTOM);
			if(packInfo != null) {
				packList.put("inmemory:jaopca", packInfo);
			}
		}
	}
}
