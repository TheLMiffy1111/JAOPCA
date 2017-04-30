package thelm.jaopca.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ModuleAbstract implements IModule {

	@Override
	public void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres) {}

	@Override
	public void setCustomProperties() {}
	
	public static int energyI(IOreEntry entry, double energy) {
		return (int)(entry.getEnergyModifier()*energy);
	}
	
	public static ItemStack getOreStack(String name, int amount) {
		ItemStack ret = OreDictionary.getOres(name).get(0).copy();
		ret.stackSize = amount;
		return ret;
	}
	
	public static ItemStack getOreStack(String prefix, IOreEntry entry, int amount) {
		return getOreStack(prefix+entry.getOreName(), amount);
	}
	
	public static ItemStack getOreStackExtra(String prefix, IOreEntry entry, int amount) {
		return getOreStack(prefix+entry.getExtra(), amount);
	}
	
	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		if(FurnaceRecipes.instance().getSmeltingResult(input) == null)
			GameRegistry.addSmelting(input.copy(), output.copy(), xp);
	}
}
