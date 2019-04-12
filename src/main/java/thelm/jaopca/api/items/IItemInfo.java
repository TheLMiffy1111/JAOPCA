package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IItemInfo extends IMaterialFormInfo<ItemMaterialForm>, IItemProvider {

	ItemMaterialForm getItem();

	@Override
	default ItemMaterialForm getMaterialForm() {
		return getItem();
	}

	@Override
	default Item asItem() {
		return getItem();
	}
}
