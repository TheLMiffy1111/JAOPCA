package thelm.jaopca.api.entities;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IEntityTypeInfo extends IMaterialFormInfo, ItemLike {

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
