package thelm.jaopca.api;

import java.util.ArrayList;

import com.google.common.collect.Lists;

/**
 * Contains a list of ItemEntry s.
 * @author TheLMiffy1111
 */
public class ItemEntryGroup implements IItemRequest {
	
	public final ArrayList<ItemEntry> entryList;
	
	public ItemEntryGroup(ItemEntry... entries) {
		entryList = Lists.<ItemEntry>newArrayList(entries);
	}
	
	public static ItemEntryGroup of(ItemEntry... entries) {
		return new ItemEntryGroup(entries);
	}
}
