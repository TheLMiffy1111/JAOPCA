package thelm.jaopca.api;

import java.util.List;

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
	public List<String> getDependencies();
	
	/**
	 * Gets the item entries of this module.
	 * @return the list of item entries of this module
	 */
	public List<ItemEntry> getItemRequests();
	
	/**
	 * Register your custom things that aren't block, items, or fluids here.
	 * The passed in ItemEntry will have an entry type of EnumEntryType.CUSTOM
	 * @param itemEntry the item entry
	 * @param allOres all ores this item entry can use
	 */
	public void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres);

	/**
	 * Set custom properties of things here.
	 */
	public void setCustomProperties();
	
	/**
	 * Register your recipes here.
	 */
	public void registerRecipes();
}
