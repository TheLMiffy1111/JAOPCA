package thelm.jaopca.items;

import java.util.function.Supplier;

import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.IMaterialFormItem;

record ItemInfo(Supplier<IMaterialFormItem> item) implements IItemInfo {

	@Override
	public IMaterialFormItem getMaterialFormItem() {
		return item.get();
	}
}
