package thelm.jaopca.api.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.block.BlockBase;
import thelm.jaopca.api.item.ItemBase;

public class Utils {

	public static final HashMap<String,ItemStack> CACHE = Maps.<String,ItemStack>newHashMap();
	public static final LinkedHashSet<String> MOD_IDS = Sets.<String>newLinkedHashSet();

	public static ItemStack getOreStack(String name, int amount) {
		if(CACHE.containsKey(name)) {
			ItemStack ret = CACHE.get(name).copy();
			ret.stackSize = amount;
			return ret;
		}

		ItemStack ret = null;
		if(!OreDictionary.getOres(name).isEmpty()) {
			List<ItemStack> list = OreDictionary.getOres(name);
			ret = list.get(0).copy();
		}

		if(ret != null) {
			CACHE.put(name, ret.copy());
			ret.stackSize = amount;
		}
		return ret;
	}

	public static ItemStack getOreStack(String prefix, IOreEntry entry, int amount) {
		if(CACHE.containsKey(prefix+entry.getOreName())) {
			ItemStack ret = CACHE.get(prefix+entry.getOreName()).copy();
			ret.stackSize = amount;
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

	public static ItemStack getOreStackExtra(String prefix, IOreEntry entry, int amount) {
		if(CACHE.containsKey(prefix+entry.getExtra())) {
			ItemStack ret = CACHE.get(prefix+entry.getExtra()).copy();
			ret.stackSize = amount;
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

	public static int energyI(IOreEntry entry, double energy) {
		return (int)(entry.getEnergyModifier()*energy);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		if(FurnaceRecipes.instance().getSmeltingResult(input) == null) {
			GameRegistry.addSmelting(input.copy(), output.copy(), xp);
		}
	}

	public static ItemStack resizeStack(ItemStack stack, int size) {
		if(stack != null) {
			ItemStack ret = stack.copy();
			ret.stackSize = size;
			return ret;
		}
		return null;
	}

	public static boolean doesOreNameExist(String name) {
		return !OreDictionary.getOres(name).isEmpty();
	}
}
