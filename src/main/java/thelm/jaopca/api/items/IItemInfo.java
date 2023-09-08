package thelm.jaopca.api.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IItemInfo extends IMaterialFormInfo, ItemLike {

	IMaterialFormItem getMaterialFormItem();

	@Override
	default Item asItem() {
		return getMaterialFormItem().toItem();
	}

	@Override
	default IMaterialForm getMaterialForm() {
		return getMaterialFormItem();
	}
}
