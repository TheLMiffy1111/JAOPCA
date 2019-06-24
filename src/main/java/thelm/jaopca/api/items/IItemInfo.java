package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IItemInfo extends IMaterialFormInfo<MaterialFormItem>, IItemProvider {

	MaterialFormItem getItem();

	@Override
	default MaterialFormItem getMaterialForm() {
		return getItem();
	}

	@Override
	default Item asItem() {
		return getItem();
	}
}
