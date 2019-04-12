package thelm.jaopca.items;

import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.ItemMaterialForm;

public class ItemInfo implements IItemInfo {

	private final ItemMaterialForm item;

	ItemInfo(ItemMaterialForm item) {
		this.item = item;
	}

	@Override
	public ItemMaterialForm getItem() {
		return item;
	}
}
