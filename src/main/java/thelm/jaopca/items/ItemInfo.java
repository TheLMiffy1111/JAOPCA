package thelm.jaopca.items;

import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.MaterialFormItem;

public class ItemInfo implements IItemInfo {

	private final MaterialFormItem item;

	ItemInfo(MaterialFormItem item) {
		this.item = item;
	}

	@Override
	public MaterialFormItem getItem() {
		return item;
	}
}
