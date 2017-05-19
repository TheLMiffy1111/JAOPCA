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
public abstract class ModuleBase {
	
	/**
	 * Gets the name of this module.
	 * @return the name of this module
	 */
	public abstract String getName();
	
	/**
	 * Gets the dependencies of this module.
	 * Return an empty list for no dependencies.
	 * @return the list of modules required for this module
	 */
	public List<String> getDependencies() {
		return Lists.<String>newArrayList();
	}

	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList();
	}
	
	/**
	 * Gets the item entries of this module.
	 * @return the list of item entries of this module
	 */
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList();
	}

	public void registerConfigs(Configuration config) {}
	
	/**
	 * Register your custom things that aren't block, items, or fluids here.
	 * The passed in ItemEntry will have an entry type of EnumEntryType.CUSTOM
	 * @param itemEntry the item entry
	 * @param allOres all ores this item entry can use
	 */
	public void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres) {}

	/**
	 * Set custom properties of things here.
	 */
	public void setCustomProperties() {}

	public void preInit() {}
	
	public void init() {}
	
	public void postInit() {}

	@Override
	public boolean equals(Object other) {
		return other instanceof ModuleBase && ((ModuleBase)other).getName().equals(getName());
	}

	@Override
	public int hashCode() {
		return ~getName().hashCode();
	}
}
