package thelm.jaopca.data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.modules.ModuleHandler;

public class DataInjector {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ListMultimap<ResourceLocation, Supplier<Block>> BLOCK_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<Item>> ITEM_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, Supplier<Fluid>> FLUID_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<ResourceLocation, Supplier<IRecipe>> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Advancement.Builder> ADVANCEMENTS_INJECT = new TreeMap<>();
	private static DataInjector instance;

	public static boolean registerBlockTag(ResourceLocation location, Supplier<Block> blockSupplier) {
		return BLOCK_TAGS_INJECT.put(location, blockSupplier);
	}

	public static boolean registerItemTag(ResourceLocation location, Supplier<Item> itemSupplier) {
		return ITEM_TAGS_INJECT.put(location, itemSupplier);
	}

	public static boolean registerFluidTag(ResourceLocation location, Supplier<Fluid> fluidSupplier) {
		return FLUID_TAGS_INJECT.put(location, fluidSupplier);
	}

	public static boolean registerRecipe(ResourceLocation location, Supplier<IRecipe> recipeSupplier) {
		return RECIPES_INJECT.putIfAbsent(location, recipeSupplier) == null;
	}

	public static boolean registerAdvancement(ResourceLocation location, Advancement.Builder advancementBuilder) {
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

	public static Set<ResourceLocation> getInjectRecipes() {
		return RECIPES_INJECT.navigableKeySet();
	}

	public static Set<ResourceLocation> getInjectAdvancements() {
		return ADVANCEMENTS_INJECT.navigableKeySet();
	}

	public static DataInjector getNewInstance(NetworkTagManager tagManager, RecipeManager recipeManager, AdvancementManager advancementManager) {
		return instance = new DataInjector(tagManager, recipeManager, advancementManager);
	}

	private final NetworkTagManager tagManager;
	private final RecipeManager recipeManager;
	private final AdvancementManager advancementManager;

	private DataInjector(NetworkTagManager tagManager, RecipeManager recipeManager, AdvancementManager advancementManager) {
		this.tagManager = tagManager;
		this.recipeManager = recipeManager;
		this.advancementManager = advancementManager;
	}

	public void injectTags(IResourceManager resourceManager) {
		NetworkTagCollection<Block> blockTags = tagManager.getBlocks();
		for(Map.Entry<ResourceLocation, Collection<Supplier<Block>>> entry : BLOCK_TAGS_INJECT.asMap().entrySet()) {
			Set<Block> blocks = entry.getValue().stream().map(Supplier::get).collect(Collectors.toSet());
			if(blockTags.get(entry.getKey()) != null) {
				Tag<Block> blockTag = blockTags.get(entry.getKey());
				blockTag.getEntries().add(new Tag.ListEntry<>(blocks));
				blockTag.getAllElements().addAll(blocks);
			}
			else {
				blockTags.register(Tag.Builder.<Block>create().addAll(blocks).build(entry.getKey()));
			}
		}
		NetworkTagCollection<Item> itemTags = tagManager.getItems();
		for(Map.Entry<ResourceLocation, Collection<Supplier<Item>>> entry : ITEM_TAGS_INJECT.asMap().entrySet()) {
			Set<Item> items = entry.getValue().stream().map(Supplier::get).collect(Collectors.toSet());
			if(itemTags.get(entry.getKey()) != null) {
				Tag<Item> itemTag = itemTags.get(entry.getKey());
				itemTag.getEntries().add(new Tag.ListEntry<>(items));
				itemTag.getAllElements().addAll(items);
			}
			else {
				itemTags.register(Tag.Builder.<Item>create().addAll(items).build(entry.getKey()));
			}
		}
		NetworkTagCollection<Fluid> fluidTags = tagManager.getFluids();
		for(Map.Entry<ResourceLocation, Collection<Supplier<Fluid>>> entry : FLUID_TAGS_INJECT.asMap().entrySet()) {
			Set<Fluid> fluids = entry.getValue().stream().map(Supplier::get).collect(Collectors.toSet());
			if(fluidTags.get(entry.getKey()) != null) {
				Tag<Fluid> fluidTag = fluidTags.get(entry.getKey());
				fluidTag.getEntries().add(new Tag.ListEntry<>(fluids));
				fluidTag.getAllElements().addAll(fluids);
			}
			else {
				fluidTags.register(Tag.Builder.<Fluid>create().addAll(fluids).build(entry.getKey()));
			}
		}
	}
	
	public void injectRecipes(IResourceManager resourceManager) {
		for(Map.Entry<ResourceLocation, Supplier<IRecipe>> entry : RECIPES_INJECT.entrySet()) {
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
			else if(recipeManager.getIds().contains(entry.getKey())) {
				LOGGER.warn("Duplicate recipe ignored with ID {}", entry.getKey());
			}
			else {
				recipeManager.addRecipe(recipe);
			}
		}
		ModuleHandler.onRecipeInjectComplete(resourceManager);
	}
	
	public void injectAdvancements(IResourceManager resourceManager) {
		try {
			Field advancementListField = Arrays.stream(AdvancementManager.class.getDeclaredFields()).filter(field->field.getType() == AdvancementList.class).findFirst().get();
			advancementListField.setAccessible(true);
			AdvancementList advancementList = (AdvancementList)advancementListField.get(advancementManager);
			advancementList.loadAdvancements(Maps.newTreeMap(ADVANCEMENTS_INJECT));
		}
		catch(Exception e) {
			LOGGER.warn("Unable to inject advancements", e);
		}
	}
}
