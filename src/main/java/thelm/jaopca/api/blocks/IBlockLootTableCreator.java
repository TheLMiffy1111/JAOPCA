package thelm.jaopca.api.blocks;

import net.minecraft.world.level.storage.loot.LootTable;

public interface IBlockLootTableCreator {

	LootTable create(IMaterialFormBlock block, IBlockFormSettings settings);
}
