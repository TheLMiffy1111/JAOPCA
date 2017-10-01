package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IProperties;

/**
 *
 * @author TheLMiffy1111
 */
public class ItemProperties implements IProperties {

	/**
	 * The default ItemProperties. DO NOT CALL ANY METHODS ON THIS FIELD.
	 */
	public static final ItemProperties DEFAULT = new ItemProperties();

	public int maxStkSize = 64;
	public boolean full3D = false;
	public EnumRarity rarity = EnumRarity.COMMON;
	public Class<? extends IItemWithProperty> itemClass = ItemBase.class;

	@Override
	public EnumEntryType getType() {
		return EnumEntryType.ITEM;
	}

	public ItemProperties setMaxStackSize(int value) {
		maxStkSize = value;
		return this;
	}

	public ItemProperties setFull3D(boolean value) {
		full3D = value;
		return this;
	}

	public ItemProperties setRarity(EnumRarity value) {
		rarity = value;
		return this;
	}

	public ItemProperties setItemClass(Class<? extends IItemWithProperty> value) {
		itemClass = value;
		return this;
	}
}
