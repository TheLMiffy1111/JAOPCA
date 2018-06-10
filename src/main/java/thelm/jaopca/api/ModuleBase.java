package thelm.jaopca.api;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;

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

	public List<String> addToPrefixBlacklist() {
		return Lists.<String>newArrayList();
	}

	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList();
	}

	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.<EnumOreType>of(EnumOreType.INGOT);
	}

	/**
	 * Gets the item entries of this module.
	 * @return the list of item entries of this module
	 */
	public List<? extends IItemRequest> getItemRequests() {
		return Lists.<IItemRequest>newArrayList();
	}

	public void registerConfigsPre(Configuration configFile) {}

	public void registerConfigs(Configuration config) {}

	@Deprecated
	public boolean blacklistCustom(ItemEntry itemEntry, IOreEntry oreEntry) {
		return false;
	}

	/**
	 * Register your custom things that aren't block, items, or fluids here.
	 * The passed in ItemEntry will have an entry type of EnumEntryType.CUSTOM
	 * @param itemEntry the item entry
	 * @param allOres all ores this item entry can use
	 */
	@Deprecated
	public void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres) {}

	public void preInit() {}

	public void init() {}

	public void postInit() {}

	public List<Pair<String, String>> remaps() {
		return Lists.<Pair<String, String>>newArrayList();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ModuleBase && ((ModuleBase)other).getName().equals(getName());
	}

	@Override
	public int hashCode() {
		return ~getName().hashCode();
	}
}
