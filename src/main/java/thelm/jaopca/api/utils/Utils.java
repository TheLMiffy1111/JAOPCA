package thelm.jaopca.api.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;
import scala.actors.threadpool.Arrays;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.block.BlockBase;
import thelm.jaopca.api.item.ItemBase;

public class Utils {

	public static final HashMap<String,ItemStack> CACHE = Maps.<String,ItemStack>newHashMap();
	public static final LinkedList<String> MOD_IDS = Lists.<String>newLinkedList();

	public static ItemStack getOreStack(String name, int amount) {
		if(CACHE.containsKey(name)) {
			ItemStack ret = CACHE.get(name).copy();
			ret.setCount(amount);
			return ret;
		}

		ItemStack ret = ItemStack.EMPTY;
		if(!OreDictionary.getOres(name).isEmpty()) {
			List<ItemStack> list = OreDictionary.getOres(name);
			ret = getPreferredStack(list);
		}

		if(!ret.isEmpty()) {
			CACHE.put(name, ret.copy());
			ret.setCount(amount);
		}
		
		return ret;
	}

	public static ItemStack getOreStack(String prefix, IOreEntry entry, int amount) {
		if(CACHE.containsKey(prefix+entry.getOreName())) {
			ItemStack ret = CACHE.get(prefix+entry.getOreName()).copy();
			ret.setCount(amount);
			return ret;
		}

		if(JAOPCAApi.BLOCKS_TABLE.contains(prefix, entry.getOreName())) {
			BlockBase b = JAOPCAApi.BLOCKS_TABLE.get(prefix, entry.getOreName());
			CACHE.put(prefix+entry.getOreName(), new ItemStack(b, 1, 0));
			return new ItemStack(b, amount, 0);
		}

		if(JAOPCAApi.ITEMS_TABLE.contains(prefix, entry.getOreName())) {
			ItemBase i = JAOPCAApi.ITEMS_TABLE.get(prefix, entry.getOreName());
			CACHE.put(prefix+entry.getOreName(), new ItemStack(i, 1, 0));
			return new ItemStack(i, amount, 0);
		}

		return getOreStack(prefix+entry.getOreName(), amount);
	}

	public static ItemStack getJAOPCAOrOreStack(String prefix, String fallback, IOreEntry entry, int amount) {
		if(CACHE.containsKey(prefix+entry.getOreName())) {
			ItemStack ret = CACHE.get(prefix+entry.getOreName()).copy();
			ret.setCount(amount);
			return ret;
		}

		if(JAOPCAApi.BLOCKS_TABLE.contains(prefix, entry.getOreName())) {
			BlockBase b = JAOPCAApi.BLOCKS_TABLE.get(prefix, entry.getOreName());
			CACHE.put(prefix+entry.getOreName(), new ItemStack(b, 1, 0));
			return new ItemStack(b, amount, 0);
		}

		if(JAOPCAApi.ITEMS_TABLE.contains(prefix, entry.getOreName())) {
			ItemBase i = JAOPCAApi.ITEMS_TABLE.get(prefix, entry.getOreName());
			CACHE.put(prefix+entry.getOreName(), new ItemStack(i, 1, 0));
			return new ItemStack(i, amount, 0);
		}
		
		return getOreStack(fallback, entry, amount);
	}

	public static ItemStack getOreStackExtra(String prefix, IOreEntry entry, int amount) {
		if(CACHE.containsKey(prefix+entry.getExtra())) {
			ItemStack ret = CACHE.get(prefix+entry.getExtra()).copy();
			ret.setCount(amount);
			return ret;
		}

		if(JAOPCAApi.BLOCKS_TABLE.contains(prefix, entry.getExtra())) {
			BlockBase b = JAOPCAApi.BLOCKS_TABLE.get(prefix, entry.getExtra());
			CACHE.put(prefix+entry.getExtra(), new ItemStack(b, 1, 0));
			return new ItemStack(b, amount, 0);
		}

		if(JAOPCAApi.ITEMS_TABLE.contains(prefix, entry.getExtra())) {
			ItemBase i = JAOPCAApi.ITEMS_TABLE.get(prefix, entry.getExtra());
			CACHE.put(prefix+entry.getExtra(), new ItemStack(i, 1, 0));
			return new ItemStack(i, amount, 0);
		}

		return getOreStack(prefix+entry.getExtra(), amount);
	}
	
	public static ItemStack getJAOPCAOrOreStackExtra(String prefix, String fallback, IOreEntry entry, int amount) {
		if(CACHE.containsKey(prefix+entry.getExtra())) {
			ItemStack ret = CACHE.get(prefix+entry.getExtra()).copy();
			ret.setCount(amount);
			return ret;
		}

		if(JAOPCAApi.BLOCKS_TABLE.contains(prefix, entry.getExtra())) {
			BlockBase b = JAOPCAApi.BLOCKS_TABLE.get(prefix, entry.getExtra());
			CACHE.put(prefix+entry.getExtra(), new ItemStack(b, 1, 0));
			return new ItemStack(b, amount, 0);
		}

		if(JAOPCAApi.ITEMS_TABLE.contains(prefix, entry.getExtra())) {
			ItemBase i = JAOPCAApi.ITEMS_TABLE.get(prefix, entry.getExtra());
			CACHE.put(prefix+entry.getExtra(), new ItemStack(i, 1, 0));
			return new ItemStack(i, amount, 0);
		}
		
		return getOreStackExtra(fallback, entry, amount);
	}

	public static int energyI(IOreEntry entry, double energy) {
		return (int)(entry.getEnergyModifier()*energy);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		if(FurnaceRecipes.instance().getSmeltingResult(input).isEmpty()) {
			GameRegistry.addSmelting(input.copy(), output.copy(), xp);
		}
	}

	public static ItemStack resizeStack(ItemStack stack, int size) {
		if(!stack.isEmpty()) {
			ItemStack ret = stack.copy();
			ret.setCount(size);
			return ret;
		}
		return ItemStack.EMPTY;
	}

	public static boolean doesOreNameExist(String name) {
		return !OreDictionary.getOres(name).isEmpty();
	}
	
	public static ItemStack getPreferredStack(Iterable<ItemStack> itemList) {
		ItemStack ret = ItemStack.EMPTY;
		int index = Integer.MAX_VALUE;
		
		for(ItemStack stack : itemList) {
			if(stack.isEmpty()) {
				continue;
			}
			
			String modId = stack.getItem().getRegistryName().getResourceDomain();
			int idex = MOD_IDS.indexOf(modId);
			
			if(idex < index) {
				index = idex;
				ret = stack;
			}
		}
		
		return ret;
	}
	
	public static String to_under_score(String camelCase) {
		if(StringUtils.isEmpty(camelCase)) {
			return "";
		}
		
		String[] strings = StringUtils.splitByCharacterTypeCamelCase(camelCase);
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < strings.length; i++) {
			ret.append(StringUtils.uncapitalize(strings[i]));
			if(i < strings.length-1) {
				ret.append('_');
			}
		}
		return ret.toString();
	}
	
	public static String toPascal(String under_score) {
		if(StringUtils.isEmpty(under_score)) {
			return "";
		}
		
		String[] strings = StringUtils.split(under_score, '_');
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < strings.length; i++) {
			ret.append(StringUtils.capitalize(strings[i]));
		}
		return ret.toString();
	}
	
	public static ItemStack parseItemStack(String input) {
		try {
			String[] split0 = input.split("@");
			Item item = Item.REGISTRY.getObject(new ResourceLocation(split0[0]));
			String[] split1 = split0[1].split("x");
			int meta = Integer.parseInt(split1[0]);
			int amount = Integer.parseInt(split1[1]);
			return new ItemStack(item, amount, meta);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ItemStack.EMPTY;
	}
	
	public static void addShapedOreRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output, input);
		ShapedOreRecipe recipe = new ShapedOreRecipe(location, output, input);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}
	
	public static void addShapelessOreRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output, input);
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(location, output, input);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}
	
	public static ResourceLocation getNameForRecipe(ItemStack output, Object... input) {
		ModContainer activeContainer = Loader.instance().activeModContainer();
		ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(), output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;
		recipeLoc = new ResourceLocation(activeContainer.getModId(), baseLoc.getResourcePath()+"_"+Arrays.hashCode(input));
		return recipeLoc;
	}
}
