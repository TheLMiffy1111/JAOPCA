package thelm.jaopca.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public interface IOreEntry {

	/**
	 * 
	 * @return The name of the ore
	 */
	public String getOreName();
	
	/**
	 * 
	 * @return The name of the extra of the ore
	 */
	public String getExtra();
	
	/**
	 * 
	 * @return The energy modifier of processing
	 */
	public double getEnergyModifier();

	/**
	 * 
	 * @return The list of blacklisted modules
	 */
	public List<String> getModuleBlacklist();
	
	/**
	 * 
	 * @return The color of the ore
	 */
	public int getColor();

	public default ItemStack getIngotStack() {
		return OreDictionary.getOres("ingot"+this.getOreName()).get(0).copy();
	}
}
