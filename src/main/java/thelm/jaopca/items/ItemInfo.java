package thelm.jaopca.items;

import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.IMaterialFormItem;

record ItemInfo(IMaterialFormItem item) implements IItemInfo {

	@Override
	public IMaterialFormItem getMaterialFormItem() {
		return item;
	}
}
