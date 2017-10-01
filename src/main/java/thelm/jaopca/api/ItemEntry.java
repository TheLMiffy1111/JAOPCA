package thelm.jaopca.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.fluid.FluidProperties;
import thelm.jaopca.api.item.ItemProperties;

/**
 *
 * @author TheLMiffy1111
 */
public class ItemEntry implements IItemRequest {

	public String name;
	public String prefix;
	public EnumEntryType type;
	public EnumSet<EnumOreType> oreTypes = EnumSet.<EnumOreType>of(EnumOreType.INGOT);
	public ModelResourceLocation itemModelLocation;
	public final LinkedHashSet<String> blacklist = Sets.<String>newLinkedHashSet();
	public final ArrayList<ModuleBase> moduleList = Lists.<ModuleBase>newArrayList();

	public IProperties properties;

	public boolean skipWhenGrouped = false;

	public ItemEntry(EnumEntryType type, String name, String oreDictPrefix, ModelResourceLocation itemModelLocation) {
		this.type = type;
		this.name = name;
		this.prefix = oreDictPrefix;
		this.itemModelLocation = itemModelLocation;
		this.properties = type.defaultPpt;
	}

	public ItemEntry(EnumEntryType type, String name, String oreDictPrefix, ModelResourceLocation itemModelLocation, Collection<String> blacklist) {
		this(type,name,oreDictPrefix,itemModelLocation);
		this.blacklist.addAll(blacklist);
	}

	public ItemEntry(EnumEntryType type, String name, ModelResourceLocation itemModelLocation) {
		this(type,name,name,itemModelLocation);
	}

	public ItemEntry(EnumEntryType type, String name, ModelResourceLocation itemModelLocation, Collection<String> blacklist) {
		this(type,name,name,itemModelLocation,blacklist);
	}

	@Deprecated
	public ItemEntry setBlockProperties(BlockProperties blockProperties) {
		return setProperties(blockProperties);
	}

	@Deprecated
	public ItemEntry setItemProperties(ItemProperties itemProperties) {
		return setProperties(itemProperties);
	}

	@Deprecated
	public ItemEntry setFluidProperties(FluidProperties fluidProperties) {
		return setProperties(fluidProperties);
	}
	
	public ItemEntry setProperties(IProperties properties) {
		if(properties.getType() != this.type) {
			throw new IllegalArgumentException("property type and entry type do not match");
		}
		this.properties = properties;
		return this;
	}

	public ItemEntry setOreTypes(EnumOreType... types) {
		oreTypes = EnumSet.<EnumOreType>copyOf(Arrays.asList(types));
		return this;
	}

	public ItemEntry skipWhenGrouped(boolean does) {
		skipWhenGrouped = does;
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof ItemEntry) {
			ItemEntry entry = (ItemEntry)other;
			return entry.name.equals(name) && entry.prefix.equals(prefix) && entry.type.equals(type);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode()&prefix.hashCode()^type.hashCode();
	}
}
