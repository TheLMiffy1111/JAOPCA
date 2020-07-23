package thelm.jaopca.api.blocks;

import net.minecraft.loot.LootTable;

public interface IBlockLootTableCreator {

	LootTable create(IMaterialFormBlock block, IBlockFormSettings settings);
}
