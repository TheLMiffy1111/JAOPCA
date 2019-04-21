package thelm.jaopca.data;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

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

	public static DataInjector getNewInstance(RecipeManager recipeManager) {
		return instance = new DataInjector(recipeManager);
	}

	private final RecipeManager recipeManager;

	private DataInjector(RecipeManager recipeManager) {
		this.recipeManager = recipeManager;
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

	public static class PackFinder implements IPackFinder {

		public static final PackFinder INSTANCE = new PackFinder();

		@Override
		public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, IFactory<T> factory) {
			T packInfo = ResourcePackInfo.createResourcePack("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				BLOCK_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Set<Block> blocks = suppliers.stream().map(Supplier::get).collect(Collectors.toSet());
					Tag<Block> tag = Tag.Builder.<Block>create().addAll(blocks).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.BLOCKS::getKey));
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Set<Item> items = suppliers.stream().map(Supplier::get).collect(Collectors.toSet());
					Tag<Item> tag = Tag.Builder.<Item>create().addAll(items).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/items/"+location.getPath()+".json"), tag.serialize(ForgeRegistries.ITEMS::getKey));
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Set<Fluid> fluids = suppliers.stream().map(Supplier::get).collect(Collectors.toSet());
					Tag<Fluid> tag = Tag.Builder.<Fluid>create().addAll(fluids).build(location);
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), tag.serialize(IRegistry.FLUID::getKey));
				});
				ADVANCEMENTS_INJECT.forEach((location, builder)->{
					pack.putJson(ResourcePackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "advancements/"+location.getPath()+".json"), builder.serialize());
				});
				return pack;
			}, (IFactory<T>)factory, ResourcePackInfo.Priority.BOTTOM);
			if(packInfo != null) {
				packList.put("inmemory:jaopca", packInfo);
			}
		}
	}
}
