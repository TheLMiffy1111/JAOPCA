package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IItemInfo extends IMaterialFormInfo, IItemProvider {

	IMaterialFormItem getMaterialFormItem();

	@Override
	default Item asItem() {
		return getMaterialFormItem().asItem();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormItem();
	}
}
