package thelm.jaopca.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thelm.jaopca.api.utils.Utils;

/**
 * A module
 * @author TheLMiffy1111
 */
public interface IModule {
	
	/**
	 * Gets the name of this module.
	 * @return the name of this module
	 */
	public String getName();
	
	/**
	 * Gets the dependencies of this module.
	 * Return an empty list for no dependencies.
	 * @return the list of modules required for this module
	 */
	public default List<String> getDependencies() {
		return Lists.<String>newArrayList();
	}

	public default List<String> getOreBlacklist() {
		return Lists.<String>newArrayList();
	}
	
	/**
	 * Gets the item entries of this module.
	 * @return the list of item entries of this module
	 */
	public default List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList();
	}

	public default void registerConfigs(Configuration config) {}
	
	/**
	 * Register your custom things that aren't block, items, or fluids here.
	 * The passed in ItemEntry will have an entry type of EnumEntryType.CUSTOM
	 * @param itemEntry the item entry
	 * @param allOres all ores this item entry can use
	 */
	public default void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres) {}

	/**
	 * Set custom properties of things here.
	 */
	public default void setCustomProperties() {}

	public default void registerPreInit() {}
	
	/**
	 * Register your recipes here.
	 */
	public void registerRecipes();
}
