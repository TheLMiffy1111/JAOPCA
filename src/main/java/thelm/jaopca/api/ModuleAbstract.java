package thelm.jaopca.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Default implementation of {@link IModule}. Also contains many utility methods.
 * @author TheLMiffy1111
 */
public abstract class ModuleAbstract implements IModule {

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList();
	}

	@Override
	public void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres) {}

	@Override
	public void setCustomProperties() {}

	public static int energyI(IOreEntry entry, double energy) {
		return (int)(entry.getEnergyModifier()*energy);
	}

	public static ItemStack getOreStack(String name, int amount) {
		if(OreDictionary.getOres(name).isEmpty()) {
			return null;
		}
		
		ItemStack ret = OreDictionary.getOres(name).get(0).copy();
		ret.stackSize = amount;
		return ret;
	}

	public static ItemStack getOreStack(String prefix, IOreEntry entry, int amount) {
		if(JAOPCAApi.BLOCKS_TABLE.contains(prefix, entry.getOreName())) {
			return new ItemStack(JAOPCAApi.BLOCKS_TABLE.get(prefix, entry.getOreName()), amount, 0);
		}

		if(JAOPCAApi.ITEMS_TABLE.contains(prefix, entry.getOreName())) {
			return new ItemStack(JAOPCAApi.ITEMS_TABLE.get(prefix, entry.getOreName()), amount, 0);
		}

		return getOreStack(prefix+entry.getOreName(), amount);
	}

	public static ItemStack getOreStackExtra(String prefix, IOreEntry entry, int amount) {
		if(JAOPCAApi.BLOCKS_TABLE.contains(prefix, entry.getExtra())) {
			return new ItemStack(JAOPCAApi.BLOCKS_TABLE.get(prefix, entry.getExtra()), amount, 0);
		}

		if(JAOPCAApi.ITEMS_TABLE.contains(prefix, entry.getExtra())) {
			return new ItemStack(JAOPCAApi.ITEMS_TABLE.get(prefix, entry.getExtra()), amount, 0);
		}
		
		return getOreStack(prefix+entry.getExtra(), amount);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		if(FurnaceRecipes.instance().getSmeltingResult(input) == null)
			GameRegistry.addSmelting(input.copy(), output.copy(), xp);
	}

	public static ItemStack resizeStack(ItemStack stack, int size) {
		if(stack != null) {
			ItemStack ret = stack.copy();
			ret.stackSize = size;
			return ret;
		}
		return null;
	}
}
