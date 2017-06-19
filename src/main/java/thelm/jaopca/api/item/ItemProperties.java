package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;

/**
 * 
 * @author TheLMiffy1111
 */
public class ItemProperties {

	/**
	 * The default ItemProperties. DO NOT CALL ANY METHODS ON THIS FIELD.
	 */
	public static final ItemProperties DEFAULT = new ItemProperties();

	public int maxStkSize = 64;
	public boolean full3D = false;
	public EnumRarity rarity = EnumRarity.COMMON;
	public Class<? extends ItemBase> itemClass = ItemBase.class;

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

	public ItemProperties setItemClass(Class<? extends ItemBase> value) {
		itemClass = value;
		return this;
	}
}
