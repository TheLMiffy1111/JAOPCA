package thelm.jaopca.items;

import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.IMaterialFormItem;

class ItemInfo implements IItemInfo {

	private final IMaterialFormItem item;

	ItemInfo(IMaterialFormItem item) {
		this.item = item;
	}

	@Override
	public IMaterialFormItem getMaterialFormItem() {
		return item;
	}
}
