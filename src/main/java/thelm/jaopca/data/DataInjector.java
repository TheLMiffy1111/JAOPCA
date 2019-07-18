package thelm.jaopca.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

public class DataInjector {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ListMultimap<ResourceLocation, Supplier<? extends Block>> BLOCK_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<? extends Item>> ITEM_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<? extends Fluid>> FLUID_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<? extends EntityType<?>>> ENTITY_TYPE_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<ResourceLocation, Supplier<? extends IRecipe<?>>> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Advancement.Builder> ADVANCEMENTS_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<? extends JsonElement>> JSONS_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<String>> STRINGS_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<? extends InputStream>> INPUT_STREAMS_INJECT = new TreeMap<>();

	public static boolean registerBlockTag(ResourceLocation location, Supplier<? extends Block> blockSupplier) {
		return BLOCK_TAGS_INJECT.put(location, blockSupplier);
	}

	public static boolean registerItemTag(ResourceLocation location, Supplier<? extends Item> itemSupplier) {
		return ITEM_TAGS_INJECT.put(location, itemSupplier);
	}

	public static boolean registerFluidTag(ResourceLocation location, Supplier<? extends Fluid> fluidSupplier) {
		return FLUID_TAGS_INJECT.put(location, fluidSupplier);
	}

	public static boolean registerEntityTypeTag(ResourceLocation location, Supplier<? extends EntityType<?>> entityTypeSupplier) {
		return ENTITY_TYPE_TAGS_INJECT.put(location, entityTypeSupplier);
	}

	public static boolean registerRecipe(ResourceLocation location, Supplier<? extends IRecipe<?>> recipeSupplier) {
		return RECIPES_INJECT.putIfAbsent(location, recipeSupplier) == null;
	}

	public static boolean registerAdvancement(ResourceLocation location, Advancement.Builder advancementBuilder) {
		return ADVANCEMENTS_INJECT.putIfAbsent(location, advancementBuilder) == null;
	}

	public static boolean injectJson(ResourceLocation location, Supplier<? extends JsonElement> supplier) {
		return JSONS_INJECT.putIfAbsent(location, supplier) == null;
	}

	public static boolean injectString(ResourceLocation location, Supplier<String> supplier) {
		return STRINGS_INJECT.putIfAbsent(location, supplier) == null;
	}

	public static boolean injectInputStream(ResourceLocation location, Supplier<? extends InputStream> supplier) {
		return INPUT_STREAMS_INJECT.putIfAbsent(location, supplier) == null;
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
					Block[] blocks = suppliers.stream().map(Supplier::get).toArray(Block[]::new);
					Tag<Block> tag = Tag.Builder.<Block>create().add(blocks).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.BLOCKS::getKey));
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Item[] items = suppliers.stream().map(Supplier::get).toArray(Item[]::new);
					Tag<Item> tag = Tag.Builder.<Item>create().add(items).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/items/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.ITEMS::getKey));
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Fluid[] fluids = suppliers.stream().map(Supplier::get).toArray(Fluid[]::new);
					Tag<Fluid> tag = Tag.Builder.<Fluid>create().add(fluids).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.FLUIDS::getKey));
				});
				ENTITY_TYPE_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					EntityType<?>[] entityTypes = suppliers.stream().map(Supplier::get).toArray(EntityType<?>[]::new);
					Tag<EntityType<?>> tag = Tag.Builder.<EntityType<?>>create().add(entityTypes).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/entity_types/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.ENTITIES::getKey));
				});
				ADVANCEMENTS_INJECT.forEach((location, builder)->{
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "advancements/"+location.getPath()+".json"), builder.serialize());
				});
				JSONS_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourcePackType.SERVER_DATA, location, supplier.get());
				});
				STRINGS_INJECT.forEach((location, supplier)->{
					pack.putString(ResourcePackType.SERVER_DATA, location, supplier.get());
				});
				INPUT_STREAMS_INJECT.forEach((location, supplier)->{
					pack.putInputStream(ResourcePackType.SERVER_DATA, location, supplier.get());
				});
				return pack;
			}, factory, ResourcePackInfo.Priority.BOTTOM);
			if(packInfo != null) {
				packList.put("inmemory:jaopca", packInfo);
			}
		}
	}
}
