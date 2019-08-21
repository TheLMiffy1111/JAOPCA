package thelm.jaopca.api.entities;

import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IEntityTypeInfo extends IMaterialFormInfo, IItemProvider {

	IMaterialFormEntityType<?> getEntityType();

	SpawnEggItem getSpawnEggItem();

	@Override
	default IMaterialFormEntityType<?> getMaterialForm() {
		return getEntityType();
	}

	@Override
	default Item asItem() {
		return getSpawnEggItem();
	}
}
