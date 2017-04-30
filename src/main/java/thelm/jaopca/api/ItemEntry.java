package thelm.jaopca.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

/**
 * 
 * @author TheLMiffy1111
 */
public class ItemEntry {

	public String name;
	public String prefix;
	public EnumEntryType type;
	public ModelResourceLocation itemModelLocation;
	public final LinkedHashSet<String> blacklist = Sets.<String>newLinkedHashSet();
	public final ArrayList<IModule> moduleList = Lists.<IModule>newArrayList();
	
	public ItemEntry(EnumEntryType type, String name, String oreDictPrefix, ModelResourceLocation itemModelLocation) {
		this.type = type;
		this.name = name;
		this.prefix = oreDictPrefix;
		this.itemModelLocation = itemModelLocation;
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
}
