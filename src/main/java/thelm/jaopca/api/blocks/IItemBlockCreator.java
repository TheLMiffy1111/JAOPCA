package thelm.jaopca.api.blocks;

import java.util.function.Supplier;

public interface IItemBlockCreator {

	ItemBlockMaterialForm create(BlockMaterialForm block, Supplier<IBlockFormSettings> settings);
}
